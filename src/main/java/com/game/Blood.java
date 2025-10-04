package com.game;

import static com.raylib.Raylib.*;

public class Blood {
	Vector2 pos = new Vector2();

	Texture spriteSheet;
	int frameCount = 7;
	int frameWidth;
	int frameHeight;

	int currentFrame = 0;
	float frameTime = 0.01f;
	float timer = 0;

	public Blood(float x, float y) {
		pos.x(x).y(y);
		spriteSheet = LoadTexture("assets/animations/player/blood.png");
		frameWidth = spriteSheet.width() / frameCount;
		frameHeight = spriteSheet.height();

	}

	public void update() {
		timer += GetFrameTime();
		if (timer >= frameTime) {
			if (currentFrame >= frameCount - 1) {
				currentFrame = frameCount - 1;
			} else {
				timer = 0;
				currentFrame++;
			}

		}
	}

	public void draw() {
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
		origin.x(frameWidth).y(frameHeight);

		Color alpha = new Color();
		alpha.r((byte) 255);
		alpha.g((byte) 255);
		alpha.b((byte) 255);
		alpha.a((byte) 180);

		DrawTexturePro(spriteSheet, sourceRec, destRec, origin, 0, alpha);

	}
}
