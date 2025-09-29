package com.game;

import static com.raylib.Colors.*;
import static com.raylib.Raylib.*;

import java.util.ArrayList;
import java.util.List;

public class Player {

	boolean walking = false;
	boolean dead = false;
	Vector2 starterPos = new Vector2();
	Vector2 pos = new Vector2();
	Vector2 vel = new Vector2();
	int speed = 5;
	int rotSpeed = 8;
	int rot = 0;

	List<Blood> bloods = new ArrayList<Blood>();

	// Vector for handling collisions. These values are used to test if player will
	// collide before actually moving
	float dx = 0, dy = 0;

	// player hitbox radius
	int radius = 30;

	boolean hittingWallX = false;
	boolean hittingWallY = false;

	Texture spriteSheet;
	int frameCount = 27;
	int frameWidth;
	int frameHeight;

	int currentFrame = 0;
	float frameTime = 0.01f;
	float timer = 0;

	float killTimer = -1;

	public Player() {
		spriteSheet = LoadTexture("assets/animations/player/playermove.png");
		frameWidth = spriteSheet.width() / frameCount;
		frameHeight = spriteSheet.height();
	}

	public void update() {
		for (Blood b : bloods) {
			b.update();
		}

		if (!dead) {
			movePlayer();
			checkCollisions();
			updateAnimation();
			dx = dy = 0;
		}

		if (killTimer > 0 && GetTime() >= killTimer) {
			pos.x(World.get().starterPos.x());
			pos.y(World.get().starterPos.y());
			dead = false;
			killTimer = -1;
		}
	}

	public void draw() {
		for (Blood b : bloods) {
			b.draw();
		}

		if (!dead) {
			Rectangle sourceRec = new Rectangle();
			sourceRec.x(frameWidth * currentFrame)
					.y(0)
					.width(frameWidth)
					.height(frameHeight);

			Rectangle destRec = new Rectangle();
			destRec.x(pos.x())
					.y(pos.y())
					.width(frameWidth * 2)
					.height(frameHeight * 2);

			Vector2 origin = new Vector2();
			origin.x(frameWidth).y(frameHeight); // pivot point for rotation
			DrawTexturePro(spriteSheet, sourceRec, destRec, origin, rot + 90, WHITE);
		}
	}

	public void updateAnimation() {
		if (walking) {
			timer += GetFrameTime();
			if (timer >= frameTime) {
				timer = 0;
				currentFrame++;
				if (currentFrame >= frameCount) {
					currentFrame = 0;
				}
			}
		} else {
			currentFrame = 0;
		}
	}

	public void movePlayer() {
		Vector2 dir = new Vector2();

		dir.x((float) Math.cos(Math.toRadians(rot)));
		dir.y((float) Math.sin(Math.toRadians(rot)));

		if (Vector2Length(dir) > 0) {
			Vector2Normalize(dir);
		}

		vel.x(dir.x() * speed);
		vel.y(dir.y() * speed);

		if (IsKeyPressed(KEY_UP))
			walking = true;
		if (IsKeyDown(KEY_RIGHT))
			rot += rotSpeed;
		if (IsKeyDown(KEY_LEFT))
			rot -= rotSpeed;
		if (walking) {
			dx += vel.x();
			dy += vel.y();
		}
	}

	public void checkCollisions() {
		hittingWallX = false;
		hittingWallY = false;

		for (Rectangle wall : World.get().walls) {
			// Test X movement
			Vector2 testX = new Vector2();
			testX.x(pos.x() + dx).y(pos.y());
			if (CheckCollisionCircleRec(testX, radius - Math.abs(dx), wall)) {
				hittingWallX = true;
			}

			// Test Y movement
			Vector2 testY = new Vector2();
			testY.x(pos.x()).y(pos.y() + dy);
			if (CheckCollisionCircleRec(testY, radius - Math.abs(dy), wall)) {
				hittingWallY = true;
			}

			if (hittingWallX && hittingWallY)
				break;
		}
		if (!hittingWallX)
			pos.x(pos.x() + dx);
		if (!hittingWallY)
			pos.y(pos.y() + dy);

		if (CheckCollisionCircleRec(pos, radius, World.get().levelEnd)) {
			walking = false;
			if (World.get().currentLevel == World.get().levelCount) {
				CloseWindow();
				System.out.println("You won!");
				System.exit(0);
			} else {
				World.get().changeLevel();
			}
		}
	}

	public void kill() {
		Blood b = new Blood(pos.x(), pos.y());
		bloods.add(b);

		dead = true;
		walking = false;
		rot = 0;

		killTimer = (float) GetTime() + 0.05f;
	}
}
