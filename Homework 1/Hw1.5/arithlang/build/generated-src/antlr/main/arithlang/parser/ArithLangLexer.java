// Generated from arithlang/parser/ArithLang.g by ANTLR 4.5
package arithlang.parser; import static arithlang.AST.*;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ArithLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, Define=6, Let=7, Dot=8, Number=9, 
		Identifier=10, Letter=11, LetterOrDigit=12, AT=13, ELLIPSIS=14, WS=15, 
		Comment=16, Line_Comment=17;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "Define", "Let", "Dot", "Number", 
		"Identifier", "Letter", "LetterOrDigit", "DIGIT", "AT", "ELLIPSIS", "WS", 
		"Comment", "Line_Comment"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'-'", "'('", "'+'", "')'", "'*'", "'define'", "'let'", "'.'", null, 
		null, null, null, "'@'", "'...'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, "Define", "Let", "Dot", "Number", 
		"Identifier", "Letter", "LetterOrDigit", "AT", "ELLIPSIS", "WS", "Comment", 
		"Line_Comment"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ArithLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ArithLang.g"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 10:
			return Letter_sempred((RuleContext)_localctx, predIndex);
		case 11:
			return LetterOrDigit_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean Letter_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return Character.isJavaIdentifierStart(_input.LA(-1));
		case 1:
			return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}
	private boolean LetterOrDigit_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return Character.isJavaIdentifierPart(_input.LA(-1));
		case 3:
			return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\23\u0082\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\3\n\6\n@\n\n\r\n\16\nA\3\13\3\13"+
		"\7\13F\n\13\f\13\16\13I\13\13\3\f\3\f\3\f\3\f\3\f\3\f\5\fQ\n\f\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\5\rY\n\r\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\21"+
		"\6\21d\n\21\r\21\16\21e\3\21\3\21\3\22\3\22\3\22\3\22\7\22n\n\22\f\22"+
		"\16\22q\13\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\7\23|\n\23"+
		"\f\23\16\23\177\13\23\3\23\3\23\3o\2\24\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\2\35\17\37\20!\21#\22%\23\3\2\t\6\2&&C"+
		"\\aac|\4\2\2\u0101\ud802\udc01\3\2\ud802\udc01\3\2\udc02\ue001\7\2&&\62"+
		";C\\aac|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\u0089\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5)\3\2"+
		"\2\2\7+\3\2\2\2\t-\3\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\178\3\2\2\2\21<\3"+
		"\2\2\2\23?\3\2\2\2\25C\3\2\2\2\27P\3\2\2\2\31X\3\2\2\2\33Z\3\2\2\2\35"+
		"\\\3\2\2\2\37^\3\2\2\2!c\3\2\2\2#i\3\2\2\2%w\3\2\2\2\'(\7/\2\2(\4\3\2"+
		"\2\2)*\7*\2\2*\6\3\2\2\2+,\7-\2\2,\b\3\2\2\2-.\7+\2\2.\n\3\2\2\2/\60\7"+
		",\2\2\60\f\3\2\2\2\61\62\7f\2\2\62\63\7g\2\2\63\64\7h\2\2\64\65\7k\2\2"+
		"\65\66\7p\2\2\66\67\7g\2\2\67\16\3\2\2\289\7n\2\29:\7g\2\2:;\7v\2\2;\20"+
		"\3\2\2\2<=\7\60\2\2=\22\3\2\2\2>@\5\33\16\2?>\3\2\2\2@A\3\2\2\2A?\3\2"+
		"\2\2AB\3\2\2\2B\24\3\2\2\2CG\5\27\f\2DF\5\31\r\2ED\3\2\2\2FI\3\2\2\2G"+
		"E\3\2\2\2GH\3\2\2\2H\26\3\2\2\2IG\3\2\2\2JQ\t\2\2\2KL\n\3\2\2LQ\6\f\2"+
		"\2MN\t\4\2\2NO\t\5\2\2OQ\6\f\3\2PJ\3\2\2\2PK\3\2\2\2PM\3\2\2\2Q\30\3\2"+
		"\2\2RY\t\6\2\2ST\n\3\2\2TY\6\r\4\2UV\t\4\2\2VW\t\5\2\2WY\6\r\5\2XR\3\2"+
		"\2\2XS\3\2\2\2XU\3\2\2\2Y\32\3\2\2\2Z[\4\62;\2[\34\3\2\2\2\\]\7B\2\2]"+
		"\36\3\2\2\2^_\7\60\2\2_`\7\60\2\2`a\7\60\2\2a \3\2\2\2bd\t\7\2\2cb\3\2"+
		"\2\2de\3\2\2\2ec\3\2\2\2ef\3\2\2\2fg\3\2\2\2gh\b\21\2\2h\"\3\2\2\2ij\7"+
		"\61\2\2jk\7,\2\2ko\3\2\2\2ln\13\2\2\2ml\3\2\2\2nq\3\2\2\2op\3\2\2\2om"+
		"\3\2\2\2pr\3\2\2\2qo\3\2\2\2rs\7,\2\2st\7\61\2\2tu\3\2\2\2uv\b\22\2\2"+
		"v$\3\2\2\2wx\7\61\2\2xy\7\61\2\2y}\3\2\2\2z|\n\b\2\2{z\3\2\2\2|\177\3"+
		"\2\2\2}{\3\2\2\2}~\3\2\2\2~\u0080\3\2\2\2\177}\3\2\2\2\u0080\u0081\b\23"+
		"\2\2\u0081&\3\2\2\2\n\2AGPXeo}\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}