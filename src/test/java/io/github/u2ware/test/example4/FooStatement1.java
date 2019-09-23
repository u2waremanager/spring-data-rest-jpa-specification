package io.github.u2ware.test.example4;

import org.springframework.stereotype.Component;

@Component("fooStatement")
public class FooStatement1 implements HibernatePrepareStatementAware{

	@Override
	public String getStatement() {
		return "'a'";
	}
}
