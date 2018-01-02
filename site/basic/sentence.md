# SENTENCE

To make ScriptBasic  business user friendly it is possible to define sentences that are converted to function
calls in the BASIC interpreter. That way the business users can program almost like writing English sentences and
what sentences mean what is defined in BASIC.

To define a sentence you can write

```
sentence "the sentence string" call methodName
```

The syntactical definition of the sentence is a string literal. The keyword `call`  separates the syntactical definition
of the sentence from the actual definition what to do. A sentence, as the definition suggests, will always call a
method as defined at the end of the command.

The method can be a `sub` or a Java defined method.

The syntax definition string should be a sentence containg words, special characters and placeholders.
The words, special characters and placeholders should be separated in the definition by one or more space.

The placeholder can be `$expression` as written here, a dollar sign immediately followed by the word "expression".

When ScriptBasic fails to syntax analyze a line as a final effort it tries ro match the line against the sentences
which are defined in the source code.

A sentence definition should precede it's use.

Matching the line against a sentence definition succeeds if the characters, words are following each other as
defined in the `sentence` commands and the `$expression` placeholders match expressions.

When a sentence is matched the syntax analyzer creates a method call to the named mathod using the expressions as
arguments. The expressions are passed from left to right.

## Examples

### Simple sentence

You can define a simple subroutine that asserts that the two arguments are equal and in case they are not then
it signals error:

```
sub assertEquals(a,b)
 if a <> b then
   error "equality assertion failed"
 endif
endsub
```

We can invoke it directly from the code calling the method by the name or we can define a sentence

```
sentence "assert that $expression is the same as $expression" call assertEquals
```

Following this line we can write

```
assert that 13+2 is the same as 15
```

This line will be recognized by the BASIC interpreter and executed as

```
assertEquals 13+2, 15
```


### Start with expression

You can start a sentence with an expression.

```
sentence "$expression is the answer" call isTheAnswer
42 is the answer


sub isTheAnswer( a )
  if a <> 42 then
    error a + " is not the answer"
  endif
endsub
```

will actually work.

You can have many sentences that start with an expression. Together with the previous example you can also have

```
sentence "$expression is exactly the answer" call isTheAnswer
42 is exactly the answer
```

in the same program.

### Start with keyword

You can also start a sentence with a BASIC keyword. 

```
sentence "for example $expression " call isTheAnswer
for example 42
```

will just work fine. However it is to note that if there is a syntax error in this line, for example

```
for exampla 42

... There is no '=' after the 'FOR'

```

the syntax analyzer will complain about the missing `=` character it expectes in a `FOR` statement.

A sentence can not start with a `'` character or with the keyword `REM` because these start comment lines and are skipped
by the syntax analysis and such a use of the `sentence` definition is also counterintuitive. Thus if you try
to create a sentence

```
sentence "rem sleep phase start" call StartRemPhase
```

in your sleep monitoring application then you will get an error starting the code (rather than just not calling the
`StartRemPhase` method).

### Start with special character

You can not only include but you can also start a sentence with some special character. For example

```
sentence ". this is $expression" call expression
.this is "a string expression"
```

defines and uses a sentence that starts with a period.