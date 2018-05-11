package com.adino.disasteraide.model;

/**
 * Created by afadinsro on 2/26/18.
 */

public enum DisasterCategory {
    FIRE{
        @Override
        public String toString() {
            return DisasterConstants.FIRE;
        }
    },
    FLOOD{
        @Override
        public String toString() {
            return DisasterConstants.FLOOD;
        }
    },
    EARTHQUAKE{
        @Override
        public String toString() {
            return DisasterConstants.EARTHQUAKE;
        }
    },
    METEOROLOGICAL{
        @Override
        public String toString() {
            return DisasterConstants.METEOROLOGICAL;
        }
    },
    MOTOR_ACCIDENT{
        @Override
        public String toString() {
            return DisasterConstants.MOTOR_ACCIDENT;
        }
    },
    EPIDEMIC{
        @Override
        public String toString() {
            return DisasterConstants.EPIDEMIC;
        }
    };

    private static class DisasterConstants{
        public static final String EARTHQUAKE = "EARTHQUAKE";
        public static final String METEOROLOGICAL = "METEOROLOGICAL";
        public static final String EPIDEMIC = "EPIDEMIC";
        public static final String FLOOD = "FLOOD";
        public static final String FIRE = "FIRE";
        public static final String MOTOR_ACCIDENT = "MOTOR ACCIDENT";
    }
}
