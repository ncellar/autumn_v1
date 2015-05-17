package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.AllReferenceResolver;
import com.norswap.autumn.parsing.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

public final class JavaGrammar
{
    public ParsingExpression root()
    {
        new AllReferenceResolver().walk(CompilationUnit);
        return CompilationUnit;
    }

    ParsingExpression CompilationUnit = named$("CompilationUnit", sequence(reference("Spacing"), optional(reference("PackageDeclaration")), zeroMore(reference("ImportDeclaration")), zeroMore(reference("TypeDeclaration")), reference("EOT")));

    ParsingExpression PackageDeclaration = named$("PackageDeclaration", sequence(zeroMore(reference("Annotation")), reference("PACKAGE"), reference("QualifiedIdentifier"), reference("SEMI")));

    ParsingExpression ImportDeclaration = named$("ImportDeclaration", choice(sequence(reference("IMPORT"), optional(reference("STATIC")), reference("QualifiedIdentifier"), optional(sequence(reference("DOT"), reference("STAR"))), reference("SEMI")), reference("SEMI")));

    ParsingExpression TypeDeclaration = named$("TypeDeclaration", choice(sequence(zeroMore(reference("Modifier")), choice(reference("ClassDeclaration"), reference("EnumDeclaration"), reference("InterfaceDeclaration"), reference("AnnotationTypeDeclaration"))), reference("SEMI")));

    ParsingExpression ClassDeclaration = named$("ClassDeclaration", sequence(reference("CLASS"), reference("Identifier"), optional(reference("TypeParameters")), optional(sequence(reference("EXTENDS"), reference("ClassType"))), optional(sequence(reference("IMPLEMENTS"), reference("ClassTypeList"))), reference("ClassBody")));

    ParsingExpression ClassBody = named$("ClassBody", sequence(reference("LWING"), zeroMore(reference("ClassBodyDeclaration")), reference("RWING")));

    ParsingExpression ClassBodyDeclaration = named$("ClassBodyDeclaration", choice(reference("SEMI"), sequence(optional(reference("STATIC")), reference("Block")), sequence(zeroMore(reference("Modifier")), reference("MemberDecl"))));

    ParsingExpression MemberDecl = named$("MemberDecl", choice(sequence(reference("TypeParameters"), reference("GenericMethodOrConstructorRest")), sequence(reference("Type"), reference("Identifier"), reference("MethodDeclaratorRest")), sequence(reference("Type"), reference("VariableDeclarators"), reference("SEMI")), sequence(reference("VOID"), reference("Identifier"), reference("VoidMethodDeclaratorRest")), sequence(reference("Identifier"), reference("ConstructorDeclaratorRest")), reference("InterfaceDeclaration"), reference("ClassDeclaration"), reference("EnumDeclaration"), reference("AnnotationTypeDeclaration")));

    ParsingExpression GenericMethodOrConstructorRest = named$("GenericMethodOrConstructorRest", choice(sequence(choice(reference("Type"), reference("VOID")), reference("Identifier"), reference("MethodDeclaratorRest")), sequence(reference("Identifier"), reference("ConstructorDeclaratorRest"))));

    ParsingExpression MethodDeclaratorRest = named$("MethodDeclaratorRest", sequence(reference("FormalParameters"), zeroMore(reference("Dim")), optional(sequence(reference("THROWS"), reference("ClassTypeList"))), choice(reference("MethodBody"), reference("SEMI"))));

    ParsingExpression VoidMethodDeclaratorRest = named$("VoidMethodDeclaratorRest", sequence(reference("FormalParameters"), optional(sequence(reference("THROWS"), reference("ClassTypeList"))), choice(reference("MethodBody"), reference("SEMI"))));

    ParsingExpression ConstructorDeclaratorRest = named$("ConstructorDeclaratorRest", sequence(reference("FormalParameters"), optional(sequence(reference("THROWS"), reference("ClassTypeList"))), reference("MethodBody")));

    ParsingExpression MethodBody = named$("MethodBody", reference("Block"));

    ParsingExpression InterfaceDeclaration = named$("InterfaceDeclaration", sequence(reference("INTERFACE"), reference("Identifier"), optional(reference("TypeParameters")), optional(sequence(reference("EXTENDS"), reference("ClassTypeList"))), reference("InterfaceBody")));

    ParsingExpression InterfaceBody = named$("InterfaceBody", sequence(reference("LWING"), zeroMore(reference("InterfaceBodyDeclaration")), reference("RWING")));

    ParsingExpression InterfaceBodyDeclaration = named$("InterfaceBodyDeclaration", choice(sequence(zeroMore(reference("Modifier")), reference("InterfaceMemberDecl")), reference("SEMI")));

    ParsingExpression InterfaceMemberDecl = named$("InterfaceMemberDecl", choice(reference("InterfaceMethodOrFieldDecl"), reference("InterfaceGenericMethodDecl"), sequence(reference("VOID"), reference("Identifier"), reference("VoidInterfaceMethodDeclaratorRest")), reference("InterfaceDeclaration"), reference("AnnotationTypeDeclaration"), reference("ClassDeclaration"), reference("EnumDeclaration")));

    ParsingExpression InterfaceMethodOrFieldDecl = named$("InterfaceMethodOrFieldDecl", sequence(reference("Type"), reference("Identifier"), reference("InterfaceMethodOrFieldRest")));

    ParsingExpression InterfaceMethodOrFieldRest = named$("InterfaceMethodOrFieldRest", choice(sequence(reference("ConstantDeclaratorsRest"), reference("SEMI")), reference("InterfaceMethodDeclaratorRest")));

    ParsingExpression InterfaceMethodDeclaratorRest = named$("InterfaceMethodDeclaratorRest", sequence(reference("FormalParameters"), zeroMore(reference("Dim")), optional(sequence(reference("THROWS"), reference("ClassTypeList"))), reference("SEMI")));

    ParsingExpression InterfaceGenericMethodDecl = named$("InterfaceGenericMethodDecl", sequence(reference("TypeParameters"), choice(reference("Type"), reference("VOID")), reference("Identifier"), reference("InterfaceMethodDeclaratorRest")));

    ParsingExpression VoidInterfaceMethodDeclaratorRest = named$("VoidInterfaceMethodDeclaratorRest", sequence(reference("FormalParameters"), optional(sequence(reference("THROWS"), reference("ClassTypeList"))), reference("SEMI")));

    ParsingExpression ConstantDeclaratorsRest = named$("ConstantDeclaratorsRest", sequence(reference("ConstantDeclaratorRest"), zeroMore(sequence(reference("COMMA"), reference("ConstantDeclarator")))));

    ParsingExpression ConstantDeclarator = named$("ConstantDeclarator", sequence(reference("Identifier"), reference("ConstantDeclaratorRest")));

    ParsingExpression ConstantDeclaratorRest = named$("ConstantDeclaratorRest", sequence(zeroMore(reference("Dim")), reference("EQU"), reference("VariableInitializer")));

    ParsingExpression EnumDeclaration = named$("EnumDeclaration", sequence(reference("ENUM"), reference("Identifier"), optional(sequence(reference("IMPLEMENTS"), reference("ClassTypeList"))), reference("EnumBody")));

    ParsingExpression EnumBody = named$("EnumBody", sequence(reference("LWING"), optional(reference("EnumConstants")), optional(reference("COMMA")), optional(reference("EnumBodyDeclarations")), reference("RWING")));

    ParsingExpression EnumConstants = named$("EnumConstants", sequence(reference("EnumConstant"), zeroMore(sequence(reference("COMMA"), reference("EnumConstant")))));

    ParsingExpression EnumConstant = named$("EnumConstant", sequence(zeroMore(reference("Annotation")), reference("Identifier"), optional(reference("Arguments")), optional(reference("ClassBody"))));

    ParsingExpression EnumBodyDeclarations = named$("EnumBodyDeclarations", sequence(reference("SEMI"), zeroMore(reference("ClassBodyDeclaration"))));

    ParsingExpression LocalVariableDeclarationStatement = named$("LocalVariableDeclarationStatement", sequence(zeroMore(choice(reference("FINAL"), reference("Annotation"))), reference("Type"), reference("VariableDeclarators"), reference("SEMI")));

    ParsingExpression VariableDeclarators = named$("VariableDeclarators", sequence(reference("VariableDeclarator"), zeroMore(sequence(reference("COMMA"), reference("VariableDeclarator")))));

    ParsingExpression VariableDeclarator = named$("VariableDeclarator", sequence(reference("Identifier"), zeroMore(reference("Dim")), optional(sequence(reference("EQU"), reference("VariableInitializer")))));

    ParsingExpression FormalParameters = named$("FormalParameters", sequence(reference("LPAR"), optional(reference("FormalParameterList")), reference("RPAR")));

    ParsingExpression FormalParameter = named$("FormalParameter", sequence(zeroMore(choice(reference("FINAL"), reference("Annotation"))), reference("Type"), reference("VariableDeclaratorId")));

    ParsingExpression LastFormalParameter = named$("LastFormalParameter", sequence(zeroMore(choice(reference("FINAL"), reference("Annotation"))), reference("Type"), reference("ELLIPSIS"), reference("VariableDeclaratorId")));

    ParsingExpression FormalParameterList = named$("FormalParameterList", choice(sequence(reference("FormalParameter"), zeroMore(sequence(reference("COMMA"), reference("FormalParameter"))), optional(sequence(reference("COMMA"), reference("LastFormalParameter")))), reference("LastFormalParameter")));

    ParsingExpression VariableDeclaratorId = named$("VariableDeclaratorId", sequence(reference("Identifier"), zeroMore(reference("Dim"))));

    ParsingExpression Block = named$("Block", sequence(reference("LWING"), reference("BlockStatements"), reference("RWING")));

    ParsingExpression BlockStatements = named$("BlockStatements", zeroMore(reference("BlockStatement")));

    ParsingExpression BlockStatement = named$("BlockStatement", choice(reference("LocalVariableDeclarationStatement"), sequence(zeroMore(reference("Modifier")), choice(reference("ClassDeclaration"), reference("EnumDeclaration"))), reference("Statement")));

    ParsingExpression Statement = named$("Statement", choice(reference("Block"), sequence(reference("ASSERT"), reference("Expression"), optional(sequence(reference("COLON"), reference("Expression"))), reference("SEMI")), sequence(reference("IF"), reference("ParExpression"), reference("Statement"), optional(sequence(reference("ELSE"), reference("Statement")))), sequence(reference("FOR"), reference("LPAR"), optional(reference("ForInit")), reference("SEMI"), optional(reference("Expression")), reference("SEMI"), optional(reference("ForUpdate")), reference("RPAR"), reference("Statement")), sequence(reference("FOR"), reference("LPAR"), reference("FormalParameter"), reference("COLON"), reference("Expression"), reference("RPAR"), reference("Statement")), sequence(reference("WHILE"), reference("ParExpression"), reference("Statement")), sequence(reference("DO"), reference("Statement"), reference("WHILE"), reference("ParExpression"), reference("SEMI")), sequence(reference("TRY"), reference("LPAR"), reference("Resource"), zeroMore(sequence(reference("SEMI"), reference("Resource"))), optional(reference("SEMI")), reference("RPAR"), reference("Block"), zeroMore(reference("Catch")), optional(reference("Finally"))), sequence(reference("TRY"), reference("Block"), choice(sequence(oneMore(reference("Catch")), optional(reference("Finally"))), reference("Finally"))), sequence(reference("SWITCH"), reference("ParExpression"), reference("LWING"), reference("SwitchBlockStatementGroups"), reference("RWING")), sequence(reference("SYNCHRONIZED"), reference("ParExpression"), reference("Block")), sequence(reference("RETURN"), optional(reference("Expression")), reference("SEMI")), sequence(reference("THROW"), reference("Expression"), reference("SEMI")), sequence(reference("BREAK"), optional(reference("Identifier")), reference("SEMI")), sequence(reference("CONTINUE"), optional(reference("Identifier")), reference("SEMI")), reference("SEMI"), sequence(reference("StatementExpression"), reference("SEMI")), sequence(reference("Identifier"), reference("COLON"), reference("Statement"))));

    ParsingExpression Resource = named$("Resource", sequence(zeroMore(choice(reference("FINAL"), reference("Annotation"))), reference("Type"), reference("VariableDeclaratorId"), reference("EQU"), reference("Expression")));

    ParsingExpression Catch = named$("Catch", sequence(reference("CATCH"), reference("LPAR"), zeroMore(choice(reference("FINAL"), reference("Annotation"))), reference("Type"), zeroMore(sequence(reference("OR"), reference("Type"))), reference("VariableDeclaratorId"), reference("RPAR"), reference("Block")));

    ParsingExpression Finally = named$("Finally", sequence(reference("FINALLY"), reference("Block")));

    ParsingExpression SwitchBlockStatementGroups = named$("SwitchBlockStatementGroups", zeroMore(reference("SwitchBlockStatementGroup")));

    ParsingExpression SwitchBlockStatementGroup = named$("SwitchBlockStatementGroup", sequence(reference("SwitchLabel"), reference("BlockStatements")));

    ParsingExpression SwitchLabel = named$("SwitchLabel", choice(sequence(reference("CASE"), reference("ConstantExpression"), reference("COLON")), sequence(reference("CASE"), reference("EnumConstantName"), reference("COLON")), sequence(reference("DEFAULT"), reference("COLON"))));

    ParsingExpression ForInit = named$("ForInit", choice(sequence(zeroMore(choice(reference("FINAL"), reference("Annotation"))), reference("Type"), reference("VariableDeclarators")), sequence(reference("StatementExpression"), zeroMore(sequence(reference("COMMA"), reference("StatementExpression"))))));

    ParsingExpression ForUpdate = named$("ForUpdate", sequence(reference("StatementExpression"), zeroMore(sequence(reference("COMMA"), reference("StatementExpression")))));

    ParsingExpression EnumConstantName = named$("EnumConstantName", reference("Identifier"));

    ParsingExpression StatementExpression = named$("StatementExpression", reference("Expression"));

    ParsingExpression ConstantExpression = named$("ConstantExpression", reference("Expression"));

    ParsingExpression Expression = named$("Expression", sequence(reference("ConditionalExpression"), zeroMore(sequence(reference("AssignmentOperator"), reference("ConditionalExpression")))));

    ParsingExpression AssignmentOperator = named$("AssignmentOperator", choice(reference("EQU"), reference("PLUSEQU"), reference("MINUSEQU"), reference("STAREQU"), reference("DIVEQU"), reference("ANDEQU"), reference("OREQU"), reference("HATEQU"), reference("MODEQU"), reference("SLEQU"), reference("SREQU"), reference("BSREQU")));

    ParsingExpression ConditionalExpression = named$("ConditionalExpression", sequence(reference("ConditionalOrExpression"), zeroMore(sequence(reference("QUERY"), reference("Expression"), reference("COLON"), reference("ConditionalOrExpression")))));

    ParsingExpression ConditionalOrExpression = named$("ConditionalOrExpression", sequence(reference("ConditionalAndExpression"), zeroMore(sequence(reference("OROR"), reference("ConditionalAndExpression")))));

    ParsingExpression ConditionalAndExpression = named$("ConditionalAndExpression", sequence(reference("InclusiveOrExpression"), zeroMore(sequence(reference("ANDAND"), reference("InclusiveOrExpression")))));

    ParsingExpression InclusiveOrExpression = named$("InclusiveOrExpression", sequence(reference("ExclusiveOrExpression"), zeroMore(sequence(reference("OR"), reference("ExclusiveOrExpression")))));

    ParsingExpression ExclusiveOrExpression = named$("ExclusiveOrExpression", sequence(reference("AndExpression"), zeroMore(sequence(reference("HAT"), reference("AndExpression")))));

    ParsingExpression AndExpression = named$("AndExpression", sequence(reference("EqualityExpression"), zeroMore(sequence(reference("AND"), reference("EqualityExpression")))));

    ParsingExpression EqualityExpression = named$("EqualityExpression", sequence(reference("RelationalExpression"), zeroMore(sequence(choice(reference("EQUAL"), reference("NOTEQUAL")), reference("RelationalExpression")))));

    ParsingExpression RelationalExpression = named$("RelationalExpression", sequence(reference("ShiftExpression"), zeroMore(choice(sequence(choice(reference("LE"), reference("GE"), reference("LT"), reference("GT")), reference("ShiftExpression")), sequence(reference("INSTANCEOF"), reference("ReferenceType"))))));

    ParsingExpression ShiftExpression = named$("ShiftExpression", sequence(reference("AdditiveExpression"), zeroMore(sequence(choice(reference("SL"), reference("SR"), reference("BSR")), reference("AdditiveExpression")))));

    ParsingExpression AdditiveExpression = named$("AdditiveExpression", sequence(reference("MultiplicativeExpression"), zeroMore(sequence(choice(reference("PLUS"), reference("MINUS")), reference("MultiplicativeExpression")))));

    ParsingExpression MultiplicativeExpression = named$("MultiplicativeExpression", sequence(reference("UnaryExpression"), zeroMore(sequence(choice(reference("STAR"), reference("DIV"), reference("MOD")), reference("UnaryExpression")))));

    ParsingExpression UnaryExpression = named$("UnaryExpression", choice(sequence(reference("PrefixOp"), reference("UnaryExpression")), sequence(reference("LPAR"), reference("Type"), reference("RPAR"), reference("UnaryExpression")), sequence(reference("Primary"), zeroMore(reference("Selector")), zeroMore(reference("PostfixOp")))));

    ParsingExpression Primary = named$("Primary", choice(reference("ParExpression"), sequence(reference("NonWildcardTypeArguments"), choice(reference("ExplicitGenericInvocationSuffix"), sequence(reference("THIS"), reference("Arguments")))), sequence(reference("THIS"), optional(reference("Arguments"))), sequence(reference("SUPER"), reference("SuperSuffix")), reference("Literal"), sequence(reference("NEW"), reference("Creator")), sequence(reference("QualifiedIdentifier"), optional(reference("IdentifierSuffix"))), sequence(reference("BasicType"), zeroMore(reference("Dim")), reference("DOT"), reference("CLASS")), sequence(reference("VOID"), reference("DOT"), reference("CLASS"))));

    ParsingExpression IdentifierSuffix = named$("IdentifierSuffix", choice(sequence(reference("LBRK"), choice(sequence(reference("RBRK"), zeroMore(reference("Dim")), reference("DOT"), reference("CLASS")), sequence(reference("Expression"), reference("RBRK")))), reference("Arguments"), sequence(reference("DOT"), choice(reference("CLASS"), reference("ExplicitGenericInvocation"), reference("THIS"), sequence(reference("SUPER"), reference("Arguments")), sequence(reference("NEW"), optional(reference("NonWildcardTypeArguments")), reference("InnerCreator"))))));

    ParsingExpression ExplicitGenericInvocation = named$("ExplicitGenericInvocation", sequence(reference("NonWildcardTypeArguments"), reference("ExplicitGenericInvocationSuffix")));

    ParsingExpression NonWildcardTypeArguments = named$("NonWildcardTypeArguments", sequence(reference("LPOINT"), reference("ReferenceType"), zeroMore(sequence(reference("COMMA"), reference("ReferenceType"))), reference("RPOINT")));

    ParsingExpression TypeArgumentsOrDiamond = named$("TypeArgumentsOrDiamond", choice(sequence(reference("LPOINT"), reference("RPOINT")), reference("TypeArguments")));

    ParsingExpression NonWildcardTypeArgumentsOrDiamond = named$("NonWildcardTypeArgumentsOrDiamond", choice(sequence(reference("LPOINT"), reference("RPOINT")), reference("NonWildcardTypeArguments")));

    ParsingExpression ExplicitGenericInvocationSuffix = named$("ExplicitGenericInvocationSuffix", choice(sequence(reference("SUPER"), reference("SuperSuffix")), sequence(reference("Identifier"), reference("Arguments"))));

    ParsingExpression PrefixOp = named$("PrefixOp", choice(reference("INC"), reference("DEC"), reference("BANG"), reference("TILDA"), reference("PLUS"), reference("MINUS")));

    ParsingExpression PostfixOp = named$("PostfixOp", choice(reference("INC"), reference("DEC")));

    ParsingExpression Selector = named$("Selector", choice(sequence(reference("DOT"), reference("Identifier"), optional(reference("Arguments"))), sequence(reference("DOT"), reference("ExplicitGenericInvocation")), sequence(reference("DOT"), reference("THIS")), sequence(reference("DOT"), reference("SUPER"), reference("SuperSuffix")), sequence(reference("DOT"), reference("NEW"), optional(reference("NonWildcardTypeArguments")), reference("InnerCreator")), reference("DimExpr")));

    ParsingExpression SuperSuffix = named$("SuperSuffix", choice(reference("Arguments"), sequence(reference("DOT"), optional(reference("NonWildcardTypeArguments")), reference("Identifier"), optional(reference("Arguments")))));

    ParsingExpression BasicType = named$("BasicType", sequence(choice(literal("byte"), literal("short"), literal("char"), literal("int"), literal("long"), literal("float"), literal("double"), literal("boolean")), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression Arguments = named$("Arguments", sequence(reference("LPAR"), optional(sequence(reference("Expression"), zeroMore(sequence(reference("COMMA"), reference("Expression"))))), reference("RPAR")));

    ParsingExpression Creator = named$("Creator", choice(sequence(choice(reference("BasicType"), reference("CreatedName")), reference("ArrayCreatorRest")), sequence(optional(reference("NonWildcardTypeArguments")), reference("CreatedName"), reference("ClassCreatorRest"))));

    ParsingExpression CreatedName = named$("CreatedName", sequence(reference("Identifier"), optional(reference("TypeArgumentsOrDiamond")), zeroMore(sequence(reference("DOT"), reference("Identifier"), optional(reference("TypeArgumentsOrDiamond"))))));

    ParsingExpression InnerCreator = named$("InnerCreator", sequence(reference("Identifier"), optional(reference("NonWildcardTypeArgumentsOrDiamond")), reference("ClassCreatorRest")));

    ParsingExpression ClassCreatorRest = named$("ClassCreatorRest", sequence(reference("Arguments"), optional(reference("ClassBody"))));

    ParsingExpression ArrayCreatorRest = named$("ArrayCreatorRest", choice(sequence(oneMore(reference("Dim")), reference("ArrayInitializer")), sequence(oneMore(reference("DimExpr")), zeroMore(reference("Dim"))), reference("Dim")));

    ParsingExpression ArrayInitializer = named$("ArrayInitializer", sequence(reference("LWING"), optional(sequence(reference("VariableInitializer"), zeroMore(sequence(reference("COMMA"), reference("VariableInitializer"))))), optional(reference("COMMA")), reference("RWING")));

    ParsingExpression VariableInitializer = named$("VariableInitializer", choice(reference("ArrayInitializer"), reference("Expression")));

    ParsingExpression ParExpression = named$("ParExpression", sequence(reference("LPAR"), reference("Expression"), reference("RPAR")));

    ParsingExpression QualifiedIdentifier = named$("QualifiedIdentifier", sequence(reference("Identifier"), zeroMore(sequence(reference("DOT"), reference("Identifier")))));

    ParsingExpression Dim = named$("Dim", sequence(reference("LBRK"), reference("RBRK")));

    ParsingExpression DimExpr = named$("DimExpr", sequence(reference("LBRK"), reference("Expression"), reference("RBRK")));

    ParsingExpression Type = named$("Type", sequence(choice(reference("BasicType"), reference("ClassType")), zeroMore(reference("Dim"))));

    ParsingExpression ReferenceType = named$("ReferenceType", choice(sequence(reference("BasicType"), oneMore(reference("Dim"))), sequence(reference("ClassType"), zeroMore(reference("Dim")))));

    ParsingExpression ClassType = named$("ClassType", sequence(reference("Identifier"), optional(reference("TypeArguments")), zeroMore(sequence(reference("DOT"), reference("Identifier"), optional(reference("TypeArguments"))))));

    ParsingExpression ClassTypeList = named$("ClassTypeList", sequence(reference("ClassType"), zeroMore(sequence(reference("COMMA"), reference("ClassType")))));

    ParsingExpression TypeArguments = named$("TypeArguments", sequence(reference("LPOINT"), reference("TypeArgument"), zeroMore(sequence(reference("COMMA"), reference("TypeArgument"))), reference("RPOINT")));

    ParsingExpression TypeArgument = named$("TypeArgument", choice(reference("ReferenceType"), sequence(reference("QUERY"), optional(sequence(choice(reference("EXTENDS"), reference("SUPER")), reference("ReferenceType"))))));

    ParsingExpression TypeParameters = named$("TypeParameters", sequence(reference("LPOINT"), reference("TypeParameter"), zeroMore(sequence(reference("COMMA"), reference("TypeParameter"))), reference("RPOINT")));

    ParsingExpression TypeParameter = named$("TypeParameter", sequence(reference("Identifier"), optional(sequence(reference("EXTENDS"), reference("Bound")))));

    ParsingExpression Bound = named$("Bound", sequence(reference("ClassType"), zeroMore(sequence(reference("AND"), reference("ClassType")))));

    ParsingExpression Modifier = named$("Modifier", choice(reference("Annotation"), sequence(choice(literal("public"), literal("protected"), literal("private"), literal("static"), literal("abstract"), literal("final"), literal("native"), literal("synchronized"), literal("transient"), literal("volatile"), literal("strictfp")), not(reference("LetterOrDigit")), reference("Spacing"))));

    ParsingExpression AnnotationTypeDeclaration = named$("AnnotationTypeDeclaration", sequence(reference("AT"), reference("INTERFACE"), reference("Identifier"), reference("AnnotationTypeBody")));

    ParsingExpression AnnotationTypeBody = named$("AnnotationTypeBody", sequence(reference("LWING"), zeroMore(reference("AnnotationTypeElementDeclaration")), reference("RWING")));

    ParsingExpression AnnotationTypeElementDeclaration = named$("AnnotationTypeElementDeclaration", choice(sequence(zeroMore(reference("Modifier")), reference("AnnotationTypeElementRest")), reference("SEMI")));

    ParsingExpression AnnotationTypeElementRest = named$("AnnotationTypeElementRest", choice(sequence(reference("Type"), reference("AnnotationMethodOrConstantRest"), reference("SEMI")), reference("ClassDeclaration"), reference("EnumDeclaration"), reference("InterfaceDeclaration"), reference("AnnotationTypeDeclaration")));

    ParsingExpression AnnotationMethodOrConstantRest = named$("AnnotationMethodOrConstantRest", choice(reference("AnnotationMethodRest"), reference("AnnotationConstantRest")));

    ParsingExpression AnnotationMethodRest = named$("AnnotationMethodRest", sequence(reference("Identifier"), reference("LPAR"), reference("RPAR"), optional(reference("DefaultValue"))));

    ParsingExpression AnnotationConstantRest = named$("AnnotationConstantRest", reference("VariableDeclarators"));

    ParsingExpression DefaultValue = named$("DefaultValue", sequence(reference("DEFAULT"), reference("ElementValue")));

    ParsingExpression Annotation = named$("Annotation", choice(reference("NormalAnnotation"), reference("SingleElementAnnotation"), reference("MarkerAnnotation")));

    ParsingExpression NormalAnnotation = named$("NormalAnnotation", sequence(reference("AT"), reference("QualifiedIdentifier"), reference("LPAR"), optional(reference("ElementValuePairs")), reference("RPAR")));

    ParsingExpression SingleElementAnnotation = named$("SingleElementAnnotation", sequence(reference("AT"), reference("QualifiedIdentifier"), reference("LPAR"), reference("ElementValue"), reference("RPAR")));

    ParsingExpression MarkerAnnotation = named$("MarkerAnnotation", sequence(reference("AT"), reference("QualifiedIdentifier")));

    ParsingExpression ElementValuePairs = named$("ElementValuePairs", sequence(reference("ElementValuePair"), zeroMore(sequence(reference("COMMA"), reference("ElementValuePair")))));

    ParsingExpression ElementValuePair = named$("ElementValuePair", sequence(reference("Identifier"), reference("EQU"), reference("ElementValue")));

    ParsingExpression ElementValue = named$("ElementValue", choice(reference("ConditionalExpression"), reference("Annotation"), reference("ElementValueArrayInitializer")));

    ParsingExpression ElementValueArrayInitializer = named$("ElementValueArrayInitializer", sequence(reference("LWING"), optional(reference("ElementValues")), optional(reference("COMMA")), reference("RWING")));

    ParsingExpression ElementValues = named$("ElementValues", sequence(reference("ElementValue"), zeroMore(sequence(reference("COMMA"), reference("ElementValue")))));

    ParsingExpression Spacing = named$("Spacing", zeroMore(choice(oneMore(charSet(" \t\r\n\u000C")), sequence(literal("/*"), zeroMore(sequence(not(literal("*/")), any())), literal("*/")), sequence(literal("//"), zeroMore(sequence(not(charSet("\r\n")), any())), charSet("\r\n")))));

    ParsingExpression Identifier = named$("Identifier", sequence(not(reference("Keyword")), reference("Letter"), zeroMore(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression Letter = named$("Letter", choice(charRange('a', 'z'), charRange('A', 'Z'), charSet("_$")));

    ParsingExpression LetterOrDigit = named$("LetterOrDigit", choice(charRange('a', 'z'), charRange('A', 'Z'), charRange('0', '9'), charSet("_$")));

    ParsingExpression Keyword = named$("Keyword", sequence(choice(literal("abstract"), literal("assert"), literal("boolean"), literal("break"), literal("byte"), literal("case"), literal("catch"), literal("char"), literal("class"), literal("const"), literal("continue"), literal("default"), literal("double"), literal("do"), literal("else"), literal("enum"), literal("extends"), literal("false"), literal("finally"), literal("final"), literal("float"), literal("for"), literal("goto"), literal("if"), literal("implements"), literal("import"), literal("interface"), literal("int"), literal("instanceof"), literal("long"), literal("native"), literal("new"), literal("null"), literal("package"), literal("private"), literal("protected"), literal("public"), literal("return"), literal("short"), literal("static"), literal("strictfp"), literal("super"), literal("switch"), literal("synchronized"), literal("this"), literal("throws"), literal("throw"), literal("transient"), literal("true"), literal("try"), literal("void"), literal("volatile"), literal("while")), not(reference("LetterOrDigit"))));

    ParsingExpression ASSERT = named$("ASSERT", sequence(literal("assert"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression BREAK = named$("BREAK", sequence(literal("break"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression CASE = named$("CASE", sequence(literal("case"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression CATCH = named$("CATCH", sequence(literal("catch"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression CLASS = named$("CLASS", sequence(literal("class"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression CONTINUE = named$("CONTINUE", sequence(literal("continue"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression DEFAULT = named$("DEFAULT", sequence(literal("default"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression DO = named$("DO", sequence(literal("do"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression ELSE = named$("ELSE", sequence(literal("else"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression ENUM = named$("ENUM", sequence(literal("enum"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression EXTENDS = named$("EXTENDS", sequence(literal("extends"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression FINALLY = named$("FINALLY", sequence(literal("finally"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression FINAL = named$("FINAL", sequence(literal("final"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression FOR = named$("FOR", sequence(literal("for"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression IF = named$("IF", sequence(literal("if"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression IMPLEMENTS = named$("IMPLEMENTS", sequence(literal("implements"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression IMPORT = named$("IMPORT", sequence(literal("import"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression INTERFACE = named$("INTERFACE", sequence(literal("interface"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression INSTANCEOF = named$("INSTANCEOF", sequence(literal("instanceof"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression NEW = named$("NEW", sequence(literal("new"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression PACKAGE = named$("PACKAGE", sequence(literal("package"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression RETURN = named$("RETURN", sequence(literal("return"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression STATIC = named$("STATIC", sequence(literal("static"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression SUPER = named$("SUPER", sequence(literal("super"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression SWITCH = named$("SWITCH", sequence(literal("switch"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression SYNCHRONIZED = named$("SYNCHRONIZED", sequence(literal("synchronized"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression THIS = named$("THIS", sequence(literal("this"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression THROWS = named$("THROWS", sequence(literal("throws"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression THROW = named$("THROW", sequence(literal("throw"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression TRY = named$("TRY", sequence(literal("try"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression VOID = named$("VOID", sequence(literal("void"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression WHILE = named$("WHILE", sequence(literal("while"), not(reference("LetterOrDigit")), reference("Spacing")));

    ParsingExpression Literal = named$("Literal", sequence(choice(reference("FloatLiteral"), reference("IntegerLiteral"), reference("CharLiteral"), reference("StringLiteral"), sequence(literal("true"), not(reference("LetterOrDigit"))), sequence(literal("false"), not(reference("LetterOrDigit"))), sequence(literal("null"), not(reference("LetterOrDigit")))), reference("Spacing")));

    ParsingExpression IntegerLiteral = named$("IntegerLiteral", sequence(choice(reference("HexNumeral"), reference("BinaryNumeral"), reference("OctalNumeral"), reference("DecimalNumeral")), optional(charSet("lL"))));

    ParsingExpression DecimalNumeral = named$("DecimalNumeral", choice(literal("0"), sequence(charRange('1', '9'), zeroMore(sequence(zeroMore(charSet("_")), charRange('0', '9'))))));

    ParsingExpression HexNumeral = named$("HexNumeral", sequence(choice(literal("0x"), literal("0X")), reference("HexDigits")));

    ParsingExpression BinaryNumeral = named$("BinaryNumeral", sequence(choice(literal("0b"), literal("0B")), charSet("01"), zeroMore(sequence(zeroMore(charSet("_")), charSet("01")))));

    ParsingExpression OctalNumeral = named$("OctalNumeral", sequence(literal("0"), oneMore(sequence(zeroMore(charSet("_")), charRange('0', '7')))));

    ParsingExpression FloatLiteral = named$("FloatLiteral", choice(reference("HexFloat"), reference("DecimalFloat")));

    ParsingExpression DecimalFloat = named$("DecimalFloat", choice(sequence(reference("Digits"), literal("."), optional(reference("Digits")), optional(reference("Exponent")), optional(charSet("fFdD"))), sequence(literal("."), reference("Digits"), optional(reference("Exponent")), optional(charSet("fFdD"))), sequence(reference("Digits"), reference("Exponent"), optional(charSet("fFdD"))), sequence(reference("Digits"), optional(reference("Exponent")), charSet("fFdD"))));

    ParsingExpression Exponent = named$("Exponent", sequence(charSet("eE"), optional(charSet("+\\-")), reference("Digits")));

    ParsingExpression HexFloat = named$("HexFloat", sequence(reference("HexSignificand"), reference("BinaryExponent"), optional(charSet("fFdD"))));

    ParsingExpression HexSignificand = named$("HexSignificand", choice(sequence(choice(literal("0x"), literal("0X")), optional(reference("HexDigits")), literal("."), reference("HexDigits")), sequence(reference("HexNumeral"), optional(literal(".")))));

    ParsingExpression BinaryExponent = named$("BinaryExponent", sequence(charSet("pP"), optional(charSet("+\\-")), reference("Digits")));

    ParsingExpression Digits = named$("Digits", sequence(charRange('0', '9'), zeroMore(sequence(zeroMore(charSet("_")), charRange('0', '9')))));

    ParsingExpression HexDigits = named$("HexDigits", sequence(reference("HexDigit"), zeroMore(sequence(zeroMore(charSet("_")), reference("HexDigit")))));

    ParsingExpression HexDigit = named$("HexDigit", choice(charRange('a', 'f'), charRange('A', 'F'), charRange('0', '9')));

    ParsingExpression CharLiteral = named$("CharLiteral", sequence(literal("'"), choice(reference("Escape"), sequence(not(charSet("'\\\n\r")), any())), literal("'")));

    ParsingExpression StringLiteral = named$("StringLiteral", sequence(literal("\""), zeroMore(choice(reference("Escape"), sequence(not(charSet("\"\\\n\r")), any()))), literal("\"")));

    ParsingExpression Escape = named$("Escape", sequence(literal("\\"), choice(charSet("btnfr\"'\\"), reference("OctalEscape"), reference("UnicodeEscape"))));

    ParsingExpression OctalEscape = named$("OctalEscape", choice(sequence(charRange('0', '3'), charRange('0', '7'), charRange('0', '7')), sequence(charRange('0', '7'), charRange('0', '7')), charRange('0', '7')));

    ParsingExpression UnicodeEscape = named$("UnicodeEscape", sequence(oneMore(literal("u")), reference("HexDigit"), reference("HexDigit"), reference("HexDigit"), reference("HexDigit")));

    ParsingExpression AT = named$("AT", sequence(literal("@"), reference("Spacing")));

    ParsingExpression AND = named$("AND", sequence(literal("&"), not(charSet("=&")), reference("Spacing")));

    ParsingExpression ANDAND = named$("ANDAND", sequence(literal("&&"), reference("Spacing")));

    ParsingExpression ANDEQU = named$("ANDEQU", sequence(literal("&="), reference("Spacing")));

    ParsingExpression BANG = named$("BANG", sequence(literal("!"), not(literal("=")), reference("Spacing")));

    ParsingExpression BSR = named$("BSR", sequence(literal(">>>"), not(literal("=")), reference("Spacing")));

    ParsingExpression BSREQU = named$("BSREQU", sequence(literal(">>>="), reference("Spacing")));

    ParsingExpression COLON = named$("COLON", sequence(literal(":"), reference("Spacing")));

    ParsingExpression COMMA = named$("COMMA", sequence(literal(","), reference("Spacing")));

    ParsingExpression DEC = named$("DEC", sequence(literal("--"), reference("Spacing")));

    ParsingExpression DIV = named$("DIV", sequence(literal("/"), not(literal("=")), reference("Spacing")));

    ParsingExpression DIVEQU = named$("DIVEQU", sequence(literal("/="), reference("Spacing")));

    ParsingExpression DOT = named$("DOT", sequence(literal("."), reference("Spacing")));

    ParsingExpression ELLIPSIS = named$("ELLIPSIS", sequence(literal("..."), reference("Spacing")));

    ParsingExpression EQU = named$("EQU", sequence(literal("="), not(literal("=")), reference("Spacing")));

    ParsingExpression EQUAL = named$("EQUAL", sequence(literal("=="), reference("Spacing")));

    ParsingExpression GE = named$("GE", sequence(literal(">="), reference("Spacing")));

    ParsingExpression GT = named$("GT", sequence(literal(">"), not(charSet("=>")), reference("Spacing")));

    ParsingExpression HAT = named$("HAT", sequence(literal("^"), not(literal("=")), reference("Spacing")));

    ParsingExpression HATEQU = named$("HATEQU", sequence(literal("^="), reference("Spacing")));

    ParsingExpression INC = named$("INC", sequence(literal("++"), reference("Spacing")));

    ParsingExpression LBRK = named$("LBRK", sequence(literal("["), reference("Spacing")));

    ParsingExpression LE = named$("LE", sequence(literal("<="), reference("Spacing")));

    ParsingExpression LPAR = named$("LPAR", sequence(literal("("), reference("Spacing")));

    ParsingExpression LPOINT = named$("LPOINT", sequence(literal("<"), reference("Spacing")));

    ParsingExpression LT = named$("LT", sequence(literal("<"), not(charSet("=<")), reference("Spacing")));

    ParsingExpression LWING = named$("LWING", sequence(literal("{"), reference("Spacing")));

    ParsingExpression MINUS = named$("MINUS", sequence(literal("-"), not(charSet("=\\-")), reference("Spacing")));

    ParsingExpression MINUSEQU = named$("MINUSEQU", sequence(literal("-="), reference("Spacing")));

    ParsingExpression MOD = named$("MOD", sequence(literal("%"), not(literal("=")), reference("Spacing")));

    ParsingExpression MODEQU = named$("MODEQU", sequence(literal("%="), reference("Spacing")));

    ParsingExpression NOTEQUAL = named$("NOTEQUAL", sequence(literal("!="), reference("Spacing")));

    ParsingExpression OR = named$("OR", sequence(literal("|"), not(charSet("=|")), reference("Spacing")));

    ParsingExpression OREQU = named$("OREQU", sequence(literal("|="), reference("Spacing")));

    ParsingExpression OROR = named$("OROR", sequence(literal("||"), reference("Spacing")));

    ParsingExpression PLUS = named$("PLUS", sequence(literal("+"), not(charSet("=+")), reference("Spacing")));

    ParsingExpression PLUSEQU = named$("PLUSEQU", sequence(literal("+="), reference("Spacing")));

    ParsingExpression QUERY = named$("QUERY", sequence(literal("?"), reference("Spacing")));

    ParsingExpression RBRK = named$("RBRK", sequence(literal("]"), reference("Spacing")));

    ParsingExpression RPAR = named$("RPAR", sequence(literal(")"), reference("Spacing")));

    ParsingExpression RPOINT = named$("RPOINT", sequence(literal(">"), reference("Spacing")));

    ParsingExpression RWING = named$("RWING", sequence(literal("}"), reference("Spacing")));

    ParsingExpression SEMI = named$("SEMI", sequence(literal(";"), reference("Spacing")));

    ParsingExpression SL = named$("SL", sequence(literal("<<"), not(literal("=")), reference("Spacing")));

    ParsingExpression SLEQU = named$("SLEQU", sequence(literal("<<="), reference("Spacing")));

    ParsingExpression SR = named$("SR", sequence(literal(">>"), not(charSet("=>")), reference("Spacing")));

    ParsingExpression SREQU = named$("SREQU", sequence(literal(">>="), reference("Spacing")));

    ParsingExpression STAR = named$("STAR", sequence(literal("*"), not(literal("=")), reference("Spacing")));

    ParsingExpression STAREQU = named$("STAREQU", sequence(literal("*="), reference("Spacing")));

    ParsingExpression TILDA = named$("TILDA", sequence(literal("~"), reference("Spacing")));

    ParsingExpression EOT = named$("EOT", not(any()));
}
