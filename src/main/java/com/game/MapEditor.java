package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import static com.game.Constants.*;

import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class MapEditor {
	public static MapEditor mapEditorInstance = null;

	Texture turretTexture;
	Texture playerTexture;
	Texture endLevelTexture;
	Texture envSpriteSheet;
	int frameWidth;
	int frameHeight;
	int frameCount = 3;
	Rectangle sourceEnvRect = new Rectangle();
	Vector2 envRectPos = new Vector2();

	List<Rectangle> cells = new ArrayList<>();
	int cellSize = 20;
	Rectangle hoveredCell = null;

	Rectangle starterPos = new Rectangle();
	Rectangle levelEnd = new Rectangle();

	List<Rectangle> floors = new ArrayList<>();
	List<Turret> turrets = new ArrayList<>();
	List<Rectangle> walls = new ArrayList<>();

	boolean waitingForTurretInput = false;
	String currentInput = "";
	int inputStage = 0;
	Rectangle pendingTurretCell = null;

	float tRotation, tInterval, tSpeed;
	String[] selections = { "wall", "floor", "turret", "player", "end" };
	int selection = 0;

	public MapEditor() {
		envSpriteSheet = LoadTexture("assets/animations/obstacles/environment.png");
		turretTexture = LoadTexture("assets/animations/obstacles/turret.png");
		playerTexture = LoadTexture("assets/animations/player/playermove.png");
		frameWidth = envSpriteSheet.width() / frameCount;
		frameHeight = envSpriteSheet.height();

		for (int i = 0; i < SCREEN_WIDTH / cellSize; i++) {
			for (int j = 0; j < SCREEN_HEIGHT / cellSize; j++) {
				Rectangle cell = new Rectangle();
				cell.x(i * cellSize).y(j * cellSize).width(cellSize).height(cellSize);
				cells.add(cell);
			}
		}
	}

	public static MapEditor get() {
		if (mapEditorInstance == null) {
			mapEditorInstance = new MapEditor();
		}
		return mapEditorInstance;
	}

	public void draw() {
		DrawText(selections[selection], 20, 20, 20, WHITE);
		for (Rectangle floor : floors) {
			drawEnvironment(floor, 1);
		}

		for (Rectangle wall : walls) {
			drawEnvironment(wall, 0);
		}

		drawEnvironment(levelEnd, 2);
		drawPlayer(starterPos);

		for (Turret turret : turrets) {
			turret.draw(0.5f);
		}

		if (waitingForTurretInput) {
			DrawRectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, Fade(BLACK, 0.5f));

			String[] prompts = {
					"Enter turret rotation (degrees): ",
					"Enter shooting interval (seconds): ",
					"Enter bullet speed: "
			};

			DrawText(prompts[inputStage], SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2 - 40, 20, WHITE);
			DrawText(currentInput + "_", SCREEN_WIDTH / 2 - 200, SCREEN_HEIGHT / 2, 20, YELLOW);
		}
	}

	public void update() {
		if (IsKeyPressed(KEY_S)) {
			saveMap("map.csv");
		}
		if (waitingForTurretInput) {
			int key = GetCharPressed();

			while (key > 0) {
				char c = (char) key;
				if ((c >= '0' && c <= '9')) {
					currentInput += c;
				}
				key = GetCharPressed();
			}

			if (IsKeyPressed(KEY_BACKSPACE) && currentInput.length() > 0) {
				currentInput = currentInput.substring(0, currentInput.length() - 1);
			}

			if (IsKeyPressed(KEY_ENTER)) {
				if (currentInput.isEmpty())
					return;

				float value = Float.parseFloat(currentInput);
				switch (inputStage) {
					case 0 -> tRotation = value;
					case 1 -> tInterval = value;
					case 2 -> {
						tSpeed = value;
						addTurret((int) pendingTurretCell.x(), (int) pendingTurretCell.y(), tRotation, tInterval,
								tSpeed);
						waitingForTurretInput = false;
						pendingTurretCell = null;
					}
				}
				inputStage++;
				currentInput = "";
			}
			return;
		}

		Vector2 mousePos = GetMousePosition();

		for (Rectangle c : cells) {
			if (CheckCollisionPointRec(mousePos, c)) {
				if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
					Rectangle placed = new Rectangle();
					placed.x(c.x()).y(c.y()).width(c.width()).height(c.height());

					String type = selections[selection];

					boolean requiresEmpty = type.equals("wall") || type.equals("floor");

					if (requiresEmpty && cellOccupied(placed))
						return;

					switch (type) {
						case "wall":
							if (!walls.contains(placed))
								walls.add(placed);
							break;
						case "floor":
							if (!floors.contains(placed))
								floors.add(placed);
							break;
						case "turret":
							if (!waitingForTurretInput && !cellOccupied(placed)) {
								waitingForTurretInput = true;
								pendingTurretCell = placed;
								inputStage = 0;
								currentInput = "";
							}
							break;
						case "player":
							starterPos = placed;
							break;
						case "end":
							levelEnd = placed;
							break;
					}
				}

				if (IsMouseButtonDown(MOUSE_BUTTON_RIGHT)) {
					eraseCell(c);
				}
			}
		}

		if (IsKeyPressed(KEY_SPACE)) {
			if (selection == selections.length - 1) {
				selection = 0;
			} else {
				selection++;
			}
		}
	}

	boolean cellOccupied(Rectangle c) {
		for (Rectangle r : floors) {
			if (r.x() == c.x() && r.y() == c.y())
				return true;
		}
		for (Rectangle r : walls) {
			if (r.x() == c.x() && r.y() == c.y())
				return true;
		}
		for (Turret t : turrets) {
			Rectangle r = t.rect;
			if (r.x() == c.x() && r.y() == c.y())
				return true;
		}
		if (starterPos != null && starterPos.x() == c.x() && starterPos.y() == c.y())
			return true;
		if (levelEnd != null && levelEnd.x() == c.x() && levelEnd.y() == c.y())
			return true;

		return false;
	}

	void eraseCell(Rectangle c) {
		floors.removeIf(r -> r.x() == c.x() && r.y() == c.y());
		walls.removeIf(r -> r.x() == c.x() && r.y() == c.y());
		turrets.removeIf(r -> r.rect.x() == c.x() && r.rect.y() == c.y());

		if (starterPos != null && starterPos.x() == c.x() && starterPos.y() == c.y()) {
			starterPos = null;
		}
		if (levelEnd != null && levelEnd.x() == c.x() && levelEnd.y() == c.y()) {
			levelEnd = null;
		}
	}

	void drawEnvironment(Rectangle envRect, int frame) {
		sourceEnvRect.x(frameWidth * frame)
				.y(0)
				.width(frameWidth)
				.height(frameHeight);

		Rectangle dest = new Rectangle();
		dest.x(envRect.x()).y(envRect.y()).width(envRect.width()).height(envRect.height());

		Vector2 origin = new Vector2();
		origin.x(0).y(0);

		DrawTexturePro(envSpriteSheet, sourceEnvRect, dest, origin, 0, WHITE);
	}

	void drawPlayer(Rectangle rect) {
		sourceEnvRect.x(frameWidth * 0)
				.y(0)
				.width(frameWidth)
				.height(frameHeight);

		Rectangle dest = new Rectangle();
		dest.x(rect.x()).y(rect.y()).width(rect.width()).height(rect.height());

		Vector2 origin = new Vector2();
		origin.x(0).y(0);

		DrawTexturePro(playerTexture, sourceEnvRect, dest, origin, 0, WHITE);

	}

	public void addTurret(int x, int y, float rot, float interval, float speed) {
		Turret t = new Turret();
		t.rect.x(x).y(y).width(cellSize)
				.height(cellSize);
		t.rot = rot;
		t.interval = (float) interval;
		t.speed = speed;
		turrets.add(t);
	}

	public void saveMap(String filename) {
		String folderPath = "src/main/resources/maps";
		File file = new File(folderPath, filename);
		try (FileWriter writer = new FileWriter(file)) {
			int cols = (int) Math.ceil((float) SCREEN_WIDTH / cellSize);
			int rows = (int) Math.ceil((float) SCREEN_HEIGHT / cellSize);

			for (int j = 0; j < rows; j++) {
				for (int i = 0; i < cols; i++) {
					float x = i * cellSize;
					float y = j * cellSize;

					String cellValue = "";

					for (Rectangle wall : walls) {
						if (wall.x() == x && wall.y() == y) {
							cellValue = "w";
							break;
						}
					}

					for (Rectangle floor : floors) {
						if (floor.x() == x && floor.y() == y) {
							cellValue = "0";
							break;
						}
					}

					if (starterPos != null && starterPos.x() == x && starterPos.y() == y) {
						cellValue = "p";
					}

					if (levelEnd != null && levelEnd.x() == x && levelEnd.y() == y) {
						cellValue = "e";
					}

					for (Turret t : turrets) {
						if (t.rect.x() == x && t.rect.y() == y) {
							cellValue = String.format("t[%.1f][%.1f][%.1f]", t.rot, t.interval, t.speed);
						}
					}

					writer.write(cellValue);
					if (i < cols - 1)
						writer.write(",");
				}
				writer.write("\n");
			}

			System.out.println("Saved");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
