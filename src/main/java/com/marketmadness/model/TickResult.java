package com.marketmadness.model;
public record TickResult(int diceSum,double settle,double makerPL,double participantPL,double makerCumPL,int[] visibleDice){}
