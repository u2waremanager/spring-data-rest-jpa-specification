package io.github.u2ware.test.example2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public @Data class FooStatement {//implements PrepareStatementContext{

	private @Autowired FooStatement1 fooStatement1;
	private @Autowired FooStatement2 fooStatement2;
	
}
