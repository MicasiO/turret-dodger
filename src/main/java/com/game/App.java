package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import static com.game.Constants.*;

/**
 * @author Mikas Tsodikov
 *
 */

public class App {

	boolean editorMode = false;

	Camera2D camera = new Camera2D();
	Vector2 screenCenter = new Vector2();

	void startGame() {
		InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Turret dodger");
		SetTargetFPS(60);

		screenCenter.x(SCREEN_WIDTH / 2).y(SCREEN_HEIGHT / 2);

		camera.offset().x(SCREEN_WIDTH / 2).y(SCREEN_HEIGHT / 2);
		camera.rotation(0);
		camera.zoom(1.0f);

		if (!editorMode) {
			// start from first level
			World.get().changeLevel();
		}

		while (!WindowShouldClose()) {
			BeginDrawing();
			BeginMode2D(camera);
			ClearBackground(BLACK);
			loop();
			EndDrawing();
		}

		CloseWindow();
	}

	void loop() {
		if (editorMode) {
			MapEditor.get().update();
			MapEditor.get().draw();

			camera.target(screenCenter);
		} else {
			World.get().update();
			World.get().draw();
			camera.target(World.get().player.pos);
		}

	}

	public static void main(String args[]) {
		App app = new App();
		if (args[0].equals("editor")) {
			app.editorMode = true;
		}

		app.startGame();
	}

}
