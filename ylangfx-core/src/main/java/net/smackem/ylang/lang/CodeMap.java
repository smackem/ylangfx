package net.smackem.ylang.lang;

abstract class CodeMap {

    private final String source;

    CodeMap(String source) {
        this.source = source;
    }

    public static CodeMap oneToOne(String source) {
        return new CodeMap(source) {
            @Override
            public Location translate(int lineNumber) {
                return new Location(lineNumber, "*");
            }
        };
    }

    public final String source() {
        return this.source;
    }

    public abstract Location translate(int lineNumber);

    public static class Location {
        private final int lineNumber;
        private final String fileName;

        Location(int lineNumber, String fileName) {
            this.lineNumber = lineNumber;
            this.fileName = fileName;
        }

        public String fileName() {
            return this.fileName;
        }

        public int lineNumber() {
            return this.lineNumber;
        }
    }
}
