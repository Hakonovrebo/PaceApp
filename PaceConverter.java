public final class PaceConverter {
    private PaceConverter() {}

    // pace "mm:ss" eller "mm.ss" -> km/t
    public static double paceToKmh(String pace) {
        int paceSec = parsePaceToSeconds(pace);
        return 3600.0 / paceSec;
    }

    // km/t (string) -> pace "mm:ss"
    public static String kmhToPace(String kmh) {
        double speed = Double.parseDouble(kmh.trim());
        if (speed <= 0) throw new IllegalArgumentException("Fart maa vere > 0");
        int paceSec = (int) Math.round(3600.0 / speed);
        return formatPace(paceSec);
    }

    public static int parsePaceToSeconds(String s) {
        s = s.trim();
        String[] parts = s.split("[\\,\\.]"); // ":" eller "."
        if (parts.length != 2) throw new IllegalArgumentException("Bruk mm:ss eller mm.ss");

        int min = Integer.parseInt(parts[0]);
        int sec = Integer.parseInt(parts[1]);

        if (min < 0) throw new IllegalArgumentException("Minutter kan ikke vere negativ");
        if (sec < 0 || sec > 59) throw new IllegalArgumentException("Sekunder maa vere 0-59");

        int total = min * 60 + sec;
        if (total == 0) throw new IllegalArgumentException("Pace kan ikke vere 0");
        return total;
    }

    public static String formatPace(int paceSec) {
        int min = paceSec / 60;
        int sec = paceSec % 60;
        return String.format("%d:%02d", min, sec);
    }
}
