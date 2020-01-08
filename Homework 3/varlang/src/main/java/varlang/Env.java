package varlang;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of an environment, which maps variables to values.
 * 
 * @author hridesh
 *
 */
public interface Env {
	Value get (String search_var);

	@SuppressWarnings("serial")
	static public class LookupException extends RuntimeException {
		LookupException(String message){
			super(message);
		}
	}
	
	static public class EmptyEnv implements Env {
		public Value get (String search_var) {
			throw new LookupException("No binding found for name: " + search_var);
		}
	}
	
	static public class ExtendEnv implements Env {
		private Env _saved_env; 
		private String _var; 
		private Value _val;
		private double _key;

		public ExtendEnv(Env saved_env, String var, Value val){
			_saved_env = saved_env;
			_var = var;
			_val = val;
		}

		public ExtendEnv(Env saved_env, String var, Value val, double key){
			_saved_env = saved_env;
			_var = var;
			_val = new Value.NumVal(((Value.NumVal) val).v() + key);
			_key = key;
		}

		public Value get (String search_var) {
			if (search_var.equals(_var))
				return _val;
			return _saved_env.get(search_var);
		}

		public Value getDec(String search_var, double key){
			if (search_var.equals(_var)){
				return new Value.NumVal(((Value.NumVal) _val).v() - key);
			}
			else if (_saved_env instanceof ExtendEnv) return ((ExtendEnv) _saved_env).getDec(search_var, key);
			else return new Value.UnitVal();
		}
	}

	static public class GlobalEnv implements Env {
		private List<String> _vars;
		private List<Value> _vals;

		public GlobalEnv(){ init(); }

		@Override
		public Value get(String search_var) {
			if (_vars.contains(search_var)) return _vals.get(_vars.indexOf(search_var));
			else return null;
		}

		public void setVal(String var, Value val){
			_vals.set(_vars.indexOf(var), val);
		}

		void init(){
			_vars = new ArrayList<String>();
			_vals = new ArrayList<Value>();
		}

		public void addVarVal(String var, Value val) { _vars.add(var); _vals.add(val); }
	}
}
