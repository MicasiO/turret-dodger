package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import java.util.ArrayList;
import java.util.List;

public class World {

	public static World worldInstance = null;

	Texture envSpriteSheet;
	int frameWidth;
	int frameHeight;
	int frameCount = 3;

	// used later for drawing proper sprite from spritesheet
	Rectangle sourceEnvRect = new Rectangle();
	Vector2 envRectPos = new Vector2();

	MapLoader loader;

	public Player player = new Player();

	// starting player position in map
	Vector2 starterPos = new Vector2();

	public List<Rectangle> floors = new ArrayList<>();
	public List<Turret> turrets = new ArrayList<>();

	public List<Rectangle> walls = new ArrayList<>();
	public Rectangle levelEnd = new Rectangle();

	public int currentLevel = 0;
	public int levelCount = 3;

	public World() {
		envSpriteSheet = LoadTexture("assets/animations/obstacles/environment.png");
		frameWidth = envSpriteSheet.width() / frameCount;
		frameHeight = envSpriteSheet.height();

	}

	// singleton pattern. world must be accessable to all classes
	public static World get() {
		if (worldInstance == null) {
			worldInstance = new World();
		}
		return worldInstance;
	}

	// reset all world entities, then initiate new values from csv map file
	public void changeLevel() {
		loader = new MapLoader();
		currentLevel++;
		turrets.clear();
		walls.clear();
		floors.clear();

		loader.loadFile(currentLevel);
		loader.initiateLevel();

		player.bloods.clear();
		player.starterPos.x(starterPos.x());
		player.starterPos.y(starterPos.y());
		player.pos.x(starterPos.x());
		player.pos.y(starterPos.y());

	}

	void update() {
		// update player
		player.update();

		// update turrets
		for (Turret turret : turrets) {
			turret.update();
		}
	}

	void draw() {
		// draw floors
		for (Rectangle floor : floors) {
			drawEnvironment(floor, 1);
		}

		// draw player
		player.draw();

		// draw walls
		for (Rectangle wall : walls) {
			drawEnvironment(wall, 0);
		}

		// draw turrets
		for (Turret turret : turrets) {
			turret.draw(1);
		}

		// draw level end
		drawEnvironment(levelEnd, 2);
	}

	void drawEnvironment(Rectangle envRect, int frame) {

		// find proper sprite from spritesheet
		sourceEnvRect.x(frameWidth * frame)
				.y(0)
				.width(frameWidth)
				.height(frameHeight);

		// set entity position and draw sprite
		envRectPos.x(envRect.x()).y(envRect.y());
		DrawTextureRec(envSpriteSheet, sourceEnvRect, envRectPos, WHITE);
	}
}
