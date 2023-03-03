package main.java.language.visitor;

import main.java.language.ast.*;
import main.java.language.type.*;


public interface Visitor<R> {

   R visit(Fun fun);
   R visit(TDecl tDecl);
   R visit(VarDecl varDecl);
   R visit(TypeDecl typeDecl);
   R visit(SortType sortType);
   R visit(Assertion assertion);


   interface TypeVisitor<R> {
      R visit(BoolType boolType);
      R visit(IntType intType);
      R visit(FloatType floatType);
//      R visit(EmptyType emptyType);
      R visit(OptionType optionType);
      R visit(VertexType vertexType);
      R visit(EdgeType edgeType);
      R visit(VoidType voidType);
      R visit(TypeVar typeVar);
      R visit(DirType dirType);
      R visit(VIdType idType);


      R visit(PairType pairType);
      R visit(ArrayType arrayType);
      R visit(SetType setType);

      R visit(RecordType recordType);
      R visit(TupleType tupleType);

      R visit(SortType sortType);


      // R visit(SetType type);
   }

   interface ExpVisitor<R> {

      R visit(ZOp zOp);
      R visit(UOp uOp);
      R visit(BOp bOp);
      R visit(TOp tOp);
      R visit(NOp nOp);
      R visit(ITE ite);
      R visit(MatchOExp match);
      R visit(Call call);
      R visit(Quantifier quantifier);
      R visit(Tuple tuple);


      interface ZOpVisitor<S> {
         S visit(Var var);
         S visit(IntLiteral intLiteral);
         S visit(Inf inf);
         S visit(Src src);
         S visit(Vertices vertices);
         S visit(None none);

         S visit(DirIn dirIn);
         S visit(DirOut dirOut);
         S visit(DirBoth dirBoth);
         S visit(DirNone dirNone);
         S visit(True aTrue);
         S visit(False aFalse);
         S visit(Epsilon epsilon);

         S visit(Msg msg);

         S visit(SingeltonLiteral singeltonLiteral);
      }

      interface UOpVisitor<S> {
         S visit(Some some);
         S visit(Not not);

         S visit(Abs abs);

         S visit(Projection projection);
         S visit(ArraySelect arraySelect);

         S visit(WeightOfEdge weightOfEdge);
         S visit(CapOfEdge capOfEdge);
         S visit(VerticesIn verticesIn);
         S visit(VerticesOut verticesOut);
         S visit(VerticesBoth verticesBoth);
         S visit(SrcOfEdge srcOfEdge);
         S visit(DestOfEdge destOfEdge);
         S visit(VValue value);
         S visit(VId id);

         S visit(Fst fst);
         S visit(Snd snd);

         S visit(EdgesOut edgesOut);
         S visit(EdgesIn edgesIn);
         S visit(EdgesBoth edgesBoth);

         S visit(Selector selector);

         S visit(SetComplement setComplement);
         S visit(SetCardinality setCardinality);
         S visit(SingeltonTuple singeltonTuple);

      }

      interface BOpVisitor<S> {
         S visit(Plus plus);
         S visit(Minus minus);
         S visit(Eq eq);
         S visit(NEq nEq);
         S visit(Lt lt);
         S visit(Gt gt);
         S visit(Gte gte);
         S visit(Min min);
         S visit(Max max);
         S visit(Pair pair);
         S visit(And and);
         S visit(Or or);

         S visit(MinOI minOI);
         S visit(MaxOI maxOI);
         S visit(MinOF minOF);
         S visit(MaxOF maxOF);

         S visit(IsMinOI isMinOI);
         S visit(IsMaxOI isMaxOI);
         S visit(IsMinOF isMinOF);
         S visit(IsMaxOF isMaxOF);

         S visit(MinPII minPII);
         S visit(MinPIF minPIF);
         S visit(MinPFI minPFI);
         S visit(MinPFF minPFF);

         S visit(MaxPII maxPII);
         S visit(MaxPIF maxPIF);
         S visit(MaxPFI maxPFI);
         S visit(MaxPFF maxPFF);

         S visit(IsMinPII isMinPII);
         S visit(IsMinPFI isMinPFI);
         S visit(IsMinPIF isMinPIF);
         S visit(IsMinPFF isMinPFF);

         S visit(IsMaxPII isMaxPII);
         S visit(IsMaxPIF isMaxPIF);
         S visit(IsMaxPFI isMaxPFI);
         S visit(IsMaxPFF isMaxPFF);

         S visit(MinPIV minPIV);
         S visit(MaxPIV maxPIV);

         S visit(IsMinPIV isMinPIV);
         S visit(IsMaxPIV isMaxPIV);

         S visit(ArrayMax arrayMax);
         S visit(ArrayMin arrayMin);

         S visit(Select select);

         S visit(SetUnion setUnion);
         S visit(SetMinus setMinus);
         S visit(SetIntersection setIntersection);
         S visit(SetMembership setMembership);
         S visit(SetSubset setSubset);
         S visit(SetProduct setProduct);


         S visit(Implication implication);

//         S visit(SetInsert setInsert);


      }

      interface TOpVisitor<S> {
         S visit(Store store);

      }

      interface NOpVisitors<S> {
         S visit(RecordTypeConstructor recordTypeConstructor);

      }




   }

   interface StVisitor<R> {
      R visit(Assignment assignment);
      R visit(IfThen ifThen);
      R visit(IfThenElse ifThenElse);
      R visit(For aFor);
      R visit(StSeq stSeq);
      R visit(MatchOSt matchOSt);
      R visit(Signal signal);
      R visit(Return aReturn);
      R visit(Skip skip);

      R visit(Decl decl);
      R visit(SetValue setValue);


   }
}

