package io.github.u2ware.test.example4;

import org.springframework.stereotype.Component;

@Component("fooStatement2")
public class FooStatement2 implements HibernatePrepareStatementAware{

	@Override
	public String getStatement() {
		return "b'";
	}
}
