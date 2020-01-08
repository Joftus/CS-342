package reflang;

public interface Heap {

    public Value ref(Value value);
    public Value deref(Value.RefVal loc);
    public Value setref(Value.RefVal loc, Value value);
    public Value free(Value.RefVal loc);



    class Heap16Bit implements Heap {
        final static int heap_size = 65536;
        Value[] _rep;

        public Heap16Bit() {
            _rep = new Value[heap_size];
        }

        @Override
        public Value ref(Value value) {
            int l = -1;
            for (int index = 0; index < heap_size; index++) {
                if (_rep[index] == null) {
                    l = index;
                    break;
                }
            }
            if (l == -1) return new Value.DynamicError("Heap is out of memory");
            Value.RefVal out = new Value.RefVal();
            _rep[l] = value;
            out.setLoc(l);
            return out;
        }

        @Override
        public Value deref(Value.RefVal loc) {
            int index = loc.loc();
            if (index < 0 || index > heap_size - 1 || _rep[index] == null) return new Value.DynamicError("Target outside of heap");
            return _rep[index];
        }

        @Override
        public Value setref(Value.RefVal loc, Value value) {
            int index = loc.loc();
            if (index < 0 || index > heap_size - 1) return new Value.DynamicError("Target outside of heap");
            _rep[index] = value;
            return value;
        }


        public Value setref(Value.NumVal loc, Value value) {
            int index = (int) loc.v();
            if (index < 0 || index > heap_size - 1) return new Value.DynamicError("Target outside of heap");
            _rep[index] = value;
            return value;
        }

        @Override
        public Value free(Value.RefVal loc) {
            int index = loc.loc();
            if (index < 0 || index > heap_size - 1) return new Value.DynamicError("Target outside of heap");
            _rep[index] = null;
            return loc;
        }
    }
}
