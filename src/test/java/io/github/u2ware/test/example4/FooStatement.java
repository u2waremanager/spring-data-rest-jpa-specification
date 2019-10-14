package io.github.u2ware.test.example4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.event.PrepareStatementContext;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
public @Data class FooStatement implements PrepareStatementContext{

	private @Autowired FooStatement1 fooStatement1;
	private @Autowired FooStatement2 fooStatement2;
	
}
