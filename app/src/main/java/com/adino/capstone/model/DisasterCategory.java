package com.adino.capstone.model;

/**
 * Created by afadinsro on 2/26/18.
 */

public enum DisasterCategory {
    FIRE{
        @Override
        public String toString() {
            return "FIRE";
        }
    },
    FLOOD{
        @Override
        public String toString() {
            return "FLOOD";
        }
    },
    EARTHQUAKE{
        @Override
        public String toString() {
            return "EARTHQUAKE";
        }
    },
    METEOROLOGICAL{
        @Override
        public String toString() {
            return "METEOROLOGICAL";
        }
    },
    MOTOR_ACCIDENT{
        @Override
        public String toString() {
            return "MOTOR_ACCIDENT";
        }
    },
    EPIDEMIC{
        @Override
        public String toString() {
            return "EPIDEMIC";
        }
    }
}
