package ch.heig.amtteam10.service.labeldetector.dao;
public record ProcessDAO(String imageUrl, int maxLabels, float minConfidence){}