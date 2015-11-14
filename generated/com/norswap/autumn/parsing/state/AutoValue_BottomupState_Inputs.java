
package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.util.Array;
import java.util.HashMap;
import java.util.HashSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_BottomupState_Inputs extends BottomupState.Inputs {

  private final int position;
  private final Array<ParsingExpression> seeded;
  private final Array<ParseChanges> seeds;
  private final HashMap<ParsingExpression, BottomupState.Precedence> precedences;
  private final Array<ExpressionCluster> history;
  private final HashSet<ParsingExpression> blocked;

  AutoValue_BottomupState_Inputs(
      int position,
      @com.norswap.util.annotations.Nullable Array<ParsingExpression> seeded,
      @com.norswap.util.annotations.Nullable Array<ParseChanges> seeds,
      HashMap<ParsingExpression, BottomupState.Precedence> precedences,
      Array<ExpressionCluster> history,
      HashSet<ParsingExpression> blocked) {
    this.position = position;
    this.seeded = seeded;
    this.seeds = seeds;
    if (precedences == null) {
      throw new NullPointerException("Null precedences");
    }
    this.precedences = precedences;
    if (history == null) {
      throw new NullPointerException("Null history");
    }
    this.history = history;
    if (blocked == null) {
      throw new NullPointerException("Null blocked");
    }
    this.blocked = blocked;
  }

  @Override
  int position() {
    return position;
  }

  @com.norswap.util.annotations.Nullable
  @Override
  Array<ParsingExpression> seeded() {
    return seeded;
  }

  @com.norswap.util.annotations.Nullable
  @Override
  Array<ParseChanges> seeds() {
    return seeds;
  }

  @Override
  HashMap<ParsingExpression, BottomupState.Precedence> precedences() {
    return precedences;
  }

  @Override
  Array<ExpressionCluster> history() {
    return history;
  }

  @Override
  HashSet<ParsingExpression> blocked() {
    return blocked;
  }

  @Override
  public String toString() {
    return "Inputs{"
        + "position=" + position + ", "
        + "seeded=" + seeded + ", "
        + "seeds=" + seeds + ", "
        + "precedences=" + precedences + ", "
        + "history=" + history + ", "
        + "blocked=" + blocked
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BottomupState.Inputs) {
      BottomupState.Inputs that = (BottomupState.Inputs) o;
      return (this.position == that.position())
           && ((this.seeded == null) ? (that.seeded() == null) : this.seeded.equals(that.seeded()))
           && ((this.seeds == null) ? (that.seeds() == null) : this.seeds.equals(that.seeds()))
           && (this.precedences.equals(that.precedences()))
           && (this.history.equals(that.history()))
           && (this.blocked.equals(that.blocked()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= position;
    h *= 1000003;
    h ^= (seeded == null) ? 0 : seeded.hashCode();
    h *= 1000003;
    h ^= (seeds == null) ? 0 : seeds.hashCode();
    h *= 1000003;
    h ^= precedences.hashCode();
    h *= 1000003;
    h ^= history.hashCode();
    h *= 1000003;
    h ^= blocked.hashCode();
    return h;
  }

}
