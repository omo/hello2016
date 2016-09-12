
import ply.lex as lex
import ply.yacc as yacc


class Parser(object):
    tokens = (
        'NUMBER', 'PLUS', 'MINUS', 'TIMES', 'LPAREN', 'RPAREN',
    )

    t_PLUS  = r'\+'
    t_MINUS = r'-'
    t_TIMES = r'\*'
    t_LPAREN = r'\('
    t_RPAREN = r'\)'

    def t_NUMBER(self, t):
        r'\d+'
        return t

    def t_newline(self, t):
        r'\n+'
        t.lexer.lineno += t.value.count("\n")

    def t_error(self, t):
        print("Illegal character '%s'" % t.value[0])
        t.lexer.skip(1)

    def __init__(self, **kwargs):
        self.debug = kwargs['debug']
        self.ply_lexer = lex.lex(module=self, debug=self.debug)
        self.ply_parser = yacc.yacc(module=self, debug=self.debug)

    def p_expression_group(self, p):
        'expression : LPAREN expression RPAREN'
        p[0] = p[2]

    def p_expression_number(self, p):
        'expression : NUMBER'
        p[0] = p[1]

    def p_expression_binop(self, p):
        """
        expression : expression PLUS expression
                   | expression MINUS expression
                   | expression TIMES expression
        """
        # print [repr(p[i]) for i in range(0,4)]
        if p[2] == '+':
            p[0] = p[1] + p[3]
        elif p[2] == '-':
            p[0] = p[1] - p[3]
        elif p[2] == '*':
            p[0] = p[1] * p[3]
        elif p[2] == '/':
            p[0] = p[1] / p[3]

    def p_error(self, p):
        if p:
            #print("Syntax error at '%s'" % p.value)
            print("Syntax error at '%r'" % p)
        else:
            print("Syntax error at EOF")

    def run(self, exp):
        return self.ply_parser.parse(input=exp, lexer=self.ply_lexer, debug=self.debug)



d=False
parser = Parser(debug=d)

print(parser.run("((10))"))
print(parser.run("(())"))
