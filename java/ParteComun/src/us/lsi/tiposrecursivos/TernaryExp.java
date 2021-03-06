package us.lsi.tiposrecursivos;

import java.util.Formatter;
import java.util.Map;
import us.lsi.common.Maps2;

public class TernaryExp<T1, T2, T3,R> extends Exp<R> {
	
	protected Exp<T1> op1 = null;
	protected Exp<T2> op2 = null;
	protected Exp<T3> op3 = null;
	protected TernaryOperator<T1,T2,T3,R> operator;
	private int id;
	private static int lastId = 0;
	private Boolean flag = false;
	
	
	
	TernaryExp(Exp<T1> op1, Exp<T2> op2, Exp<T3> op3, TernaryOperator<T1,T2,T3,R> operator) {
		super();
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
		this.operator = operator;
		this.id = lastId;
		lastId++;
	}
	
	@Override
	public Integer getArity() {
		return 3;
	}
	
	public Exp<T1> getOp1() {
		return this.op1;
	}

	public void setOp1(Exp<T1> op1) {
		this.op1 = op1;
	}

	public Exp<T2> getOp2() {
		return this.op2;
	}

	public void setOp2(Exp<T2> op2) {
		this.op2 = op2;
	}

	public Exp<T3> getOp3() {
		return op3;
	}

	public void setOp3(Exp<T3> op3) {
		this.op3 = op3;
	}

	public void setOp(Exp<T1> op1, Exp<T2> op2, Exp<T3> op3) {
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
	}
	
	public TernaryOperator<T1, T2, T3, R> getOperator() {
		return operator;
	}
	
	@Override
	public R getValue() {
		return operator.getCode().apply(op1.getValue(), op2.getValue(),op3.getValue());
	}
	
	public String toString() {
		Formatter fm = new Formatter();
		String r =  fm.format(this.operator.getFormat(), this.op1.toString(),this.op2.toString(), this.op3.toString()).toString();
		fm.close();
		return r;
	}

	@Override
	public Exp<R> copy() {
		return Exp.ternary(this.getOp1().copy(), this.getOp2().copy(),this.getOp3().copy(), this.operator);
	}

	@Override
	public Boolean isTernary() {
		return true;
	}
	
	@Override
	public Boolean isConstant() {
		return this.op1.isConstant() && this.op2.isConstant() && this.op3.isConstant();
	}

	@Override
	public Exp<R> simplify() {
		Exp<R> r;
		if(this.op1.isConstant() && this.op2.isConstant()){
			r = Exp.constant(this.getValue());
		} else {
			r = Exp.ternary(op1.simplify(),op2.simplify(),op3.simplify(),operator);
		} 
		return r;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((op1 == null) ? 0 : op1.hashCode());
		result = prime * result + ((op2 == null) ? 0 : op2.hashCode());
		result = prime * result + ((op3 == null) ? 0 : op3.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TernaryExp))
			return false;
		TernaryExp<?,?,?,?> other = (TernaryExp<?,?,?,?>) obj;
		if (op1 == null) {
			if (other.op1 != null)
				return false;
		} else if (!op1.equals(other.op1))
			return false;
		if (op2 == null) {
			if (other.op2 != null)
				return false;
		} else if (!op2.equals(other.op2))
			return false;
		if (op3 == null) {
			if (other.op3 != null)
				return false;
		} else if (!op3.equals(other.op3))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		return true;
	}

	@Override
	public Boolean match(Exp<?> exp) {
		Boolean r = false;
		if(exp.isFree()){
			exp.<R>asFree().setExp(this);
			r = true;
		} else if(exp.isTernary()){
			TernaryExp<?,?,?,?> t = exp.asTernary();
			r = this.operator.getId().equals(t.getOperator().getId());
			r = r && this.op1.match(t.op1) && this.op2.match(t.op2) && this.op3.match(t.op3);
		}
		return r;
	}

	@Override
	protected String getId() {
		return "TE_"+id;
	}

	@Override
	protected void toDOT(String file) {
		if (!flag) {
			String s1 = "    \"%s\" [label=\"%s\"];";
			Element.getFile().println(
					String.format(s1, getId(), this.operator.getName()));
			String s2 = "    \"%s\" -> \"%s\";";
			Element.getFile().println(
					String.format(s2, getId(), op1.getId()));
			Element.getFile().println(
					String.format(s2, getId(), op2.getId()));
			Element.getFile().println(
					String.format(s2, getId(), op3.getId()));
			op1.toDOT(file);
			op2.toDOT(file);
			op3.toDOT(file);
		}
		flag = true;
	}
	
	@Override
	protected void setFlagFalse() {
		flag = false;
		op1.setFlagFalse();
		op2.setFlagFalse();
		op3.setFlagFalse();
	}

	@Override
	public ExpType getType() {
		return op2.getType();
	}
	
	@Override
	protected Map<String, Exp<Object>> vars() {
		Map<String, Exp<Object>> r = Maps2.newHashMap(op1.vars());
		r.putAll(op2.vars());
		r.putAll(op3.vars());;
		return r;
	}
	
	@Override
	public Integer getPriority() {
		return 12;
	}

	@Override
	public Exp<Object> ifMatchTransform(Exp<Object> pattern, Map<String,Exp<Object>> vars, String patternResult) {
		Exp<Object> r1 = op1.ifMatchTransform(pattern, vars,patternResult);
		Exp<Object> r2 = op2.ifMatchTransform(pattern, vars,patternResult);
		Exp<Object> r3 = op3.ifMatchTransform(pattern, vars, patternResult);
		@SuppressWarnings("unchecked")
		Exp<Object> r = Exp.ternary(r1,r2,r3,(TernaryOperator<Object,Object,Object,Object>)this.operator);
		Exp<Object> copy = pattern.copy();
		if(r.match(copy)){
			Map<String,Exp<Object>> m = copy.getVars();
			Map<String,Exp<Object>> m2 = 
					Maps2.<String,Exp<Object>,Exp<Object>>newHashMap(m, 
							x->x.isFree()?x.asFree().getExp():x);
			m2.putAll(vars);
			r = Exp.parse(patternResult,m2);
		}
		return r;
	}
	
	
	@Override
	public Boolean isPattern() {
		return this.op1.isPattern() || this.op2.isPattern() || this.op3.isPattern();
	}
}
