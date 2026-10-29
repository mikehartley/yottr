package uk.co.yottr.model;

/**
 * What a member is on Yottr as: crew looking for boats, an owner looking for crew, or both.
 */
public record Sides(boolean crew, boolean owner) {
}
