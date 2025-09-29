package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import static com.game.Constants.*;

public class App {

	Camera2D camera = new Camera2D();

	void startGame() {
		InitWindow(SCREEN_WIDTH, SCREEN_HEIGHT, "Turret dodger");
		SetTargetFPS(60);

		// setup camera
		camera.offset().x(SCREEN_WIDTH / 2).y(SCREEN_HEIGHT / 2);
		camera.rotation(0);
		camera.zoom(1.0f);

		// start from first level
		World.get().changeLevel();

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
		World.get().update();
		World.get().draw();

		// camera follow player
		camera.target(World.get().player.pos);

	}

	public static void main(String args[]) {
		App app = new App();
		app.startGame();
	}

}
