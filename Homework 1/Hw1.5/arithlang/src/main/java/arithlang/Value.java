package arithlang;

public interface Value {
    public String toString();
    static class NumVal implements Value {
        private double _val;
        private String _txt;
        public NumVal(double v) { _val = v; }
        public NumVal(String s) { _txt = s; }
        public double v() { return _val; }
        public String t() { return _txt; }
        public String toString() {
            return _txt;
        }
    }

    static class DynamicError implements Value {
        private String _errorMsg;
        public DynamicError(String v) {
            _errorMsg = v;
        }
        public String v() {
            return _errorMsg;
        }
        public String toString() {
            String tmp = _errorMsg;
            if (tmp == _errorMsg) return "" + tmp;
            return "" + _errorMsg;
        }
    }
}
