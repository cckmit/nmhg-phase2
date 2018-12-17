package tavant.twms.domain.failurestruct;

import java.util.HashMap;
import java.util.Map;

public class CodeGeneratorFactory {
	Map<CodeGeneratorType,CodeGenerator> generators = new HashMap<CodeGeneratorType,CodeGenerator>();
	
	public CodeGeneratorFactory() {
		generators.put(CodeGeneratorType.APLPA_TWO_CHARS, new AlphaCodeGenerator());
		generators.put(CodeGeneratorType.NUMERIC_THREE_DIGIT_ZERO_PADDED, new NumericCodeGenerator());
	}
	
	public CodeGenerator getGenerator(CodeGeneratorType type) {
		return generators.get(type);
	}
}
