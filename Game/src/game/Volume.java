package game;

public enum Volume {

        MUTE, 
        LOW,
        LOW_MEDIUM,
        MEDIUM,
        MEDIUM_HIGH,
        HIGH;
        
        public float getGain() {
            if (this.equals(Volume.LOW)) {
                return -25.0f;
            } else if (this.equals(Volume.LOW_MEDIUM)) {
                return -20.0f;
            } else if (this.equals(Volume.MEDIUM)) {
                return -10.0f;
            } else if (this.equals(Volume.MEDIUM_HIGH)) {
                return -2.0f;
            } else if (this.equals(Volume.HIGH)) {
                return 2.0f;
            } else {
                return 0.0f;
            }
        }

}