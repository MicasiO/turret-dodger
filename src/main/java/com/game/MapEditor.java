package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import static com.game.Constants.*;

import java.util.ArrayList;
import java.util.List;

public class MapEditor {
	public static MapEditor mapEditorInstance = null;

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
	List<Rectangle> turrets = new ArrayList<>();
	List<Rectangle> walls = new ArrayList<>();

	String[] selections = { "wall", "floor", "turret", "player", "end" };
	int selection = 0;

	public MapEditor() {
		envSpriteSheet = LoadTexture("assets/animations/obstacles/environment.png");
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
		/*
		 * for (Rectangle c : cells) {
		 * DrawRectangleLines((int) c.x(), (int) c.y(), (int) c.width(), (int)
		 * c.height(), RED);
		 * }
		 */
		DrawText(selections[selection], 20, 20, 20, WHITE);
		for (Rectangle floor : floors) {
			drawEnvironment(floor, 1);
		}

		// draw walls
		for (Rectangle wall : walls) {
			drawEnvironment(wall, 0);
		}

	}

	public void update() {
		Vector2 mousePos = GetMousePosition();

		for (Rectangle c : cells) {
			if (CheckCollisionPointRec(mousePos, c)) {
				if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
					Rectangle placed = new Rectangle();
					placed.x(c.x()).y(c.y()).width(c.width()).height(c.height());

					if (selections[selection].equals("wall")) {
						if (!walls.contains(placed)) {
							walls.add(placed);
						}
					}
					if (selections[selection].equals("floor")) {
						if (!floors.contains(placed)) {
							floors.add(placed);
						}
					}
					if (selections[selection].equals("turret")) {
						if (!turrets.contains(placed)) {
							turrets.add(placed);
						}
					}
					if (selections[selection].equals("player")) {
						starterPos = placed;
					}
					if (selections[selection].equals("end")) {
						levelEnd = placed;
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

	void eraseCell(Rectangle c) {
		floors.removeIf(r -> r.x() == c.x() && r.y() == c.y());
		walls.removeIf(r -> r.x() == c.x() && r.y() == c.y());
		turrets.removeIf(r -> r.x() == c.x() && r.y() == c.y());

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
}
