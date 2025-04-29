package com.marketmadness.model;

import java.security.SecureRandom;

public class DiceSet {
    private final SecureRandom rng = new SecureRandom();
    private final int[] faces = new int[3];

    public void roll() {
        for (int i = 0; i < 3; i++) {
            faces[i] = rng.nextBoolean() ? -1 : rng.nextInt(6) + 1;
        }
        if (faces[0] != -1 && faces[1] != -1 && faces[2] != -1) {
            faces[rng.nextInt(3)] = -1;
        }
    }

    public int realise() {
        int sum = 0;
        for (int v : faces) {
            sum += v == -1 ? rng.nextInt(6) + 1 : v;
        }
        return sum;
    }

    public int[] visible() {
        return faces.clone();
    }
}