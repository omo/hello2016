
import ply.lex as lex
import ply.yacc as yacc

class Parser(object):
    t_ignore  = ' \t\r\n'

    reserved = {
        'select': 'SELECT',
        'from': 'FROM',
        'where': 'WHERE'
    }

    tokens = [
#        'NUMBER', 'PLUS', 'MINUS', 'TIMES', 'LPAREN', 'RPAREN',
        'ID', 'COMMA'
    ] + list(reserved.values())

    t_COMMA = r','

    def t_ID(self, t):
        r'[a-zA-Z_][a-zA-Z_0-9]*'
        t.type = self.reserved.get(t.value,'ID')
        return t

    def t_error(self, t):
        print("Illegal character '%s'" % t.value[0])
        t.lexer.skip(1)

    def p_select_stmt(self, p):
        """
        select_stmt : select_clause
                    | select_clause from_clause where_clause
        """
        if len(p) == 2:
            p[0] = ('select', p[1])
        else:
            p[0] = ('select', p[1], p[2], p[3])

    def p_select_clause(self, p):
        'select_clause : SELECT select_list'
        p[0] = ('project', p[2])

    def p_where_clause(self, p):
        """
        where_clause : WHERE exp
                    |
        """
        if len(p) > 1:
            p[0] = ('where', p[2])

    def p_from_clause(self, p):
        'from_clause : FROM ID'
        # XXX: could be a table list or more.
        p[0] = ('from', p[2])

    def p_select_list(self, p):
        """
        select_list : select_list_item
                    | select_list COMMA select_list_item
        """
        if len(p) == 2:
            p[0] = (p[1],)
        else:
            p[0] = p[1] + (p[3],)

    def p_select_list_item(self, p):
        """
        select_list_item : exp
        """
        # XXX: Needs "AS" handling.
        p[0] = p[1]

    def p_exp(self, p):
        """
        exp : ID
        """
        # XXX: Needs a lot more.
        p[0] = ('id', p[1])

    def p_error(self, p):
        if p:
            print("Syntax error at '%r'" % p)
        else:
            print("Syntax error at EOF")

    def __init__(self, **kwargs):
        self.debug = kwargs['debug']
        self.ply_lexer = lex.lex(module=self, debug=self.debug)
        self.ply_parser = yacc.yacc(module=self, debug=self.debug)

    def parse(self, stmt):
        return self.ply_parser.parse(input=stmt, lexer=self.ply_lexer, debug=self.debug)


d=False
parser = Parser(debug=d)

print(parser.parse('SELECT foo, bar'.lower()))
print(parser.parse('SELECT foo, bar FROM a WHERE x'.lower()))
