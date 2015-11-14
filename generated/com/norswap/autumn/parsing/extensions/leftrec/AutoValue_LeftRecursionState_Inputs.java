
package com.norswap.autumn.parsing.extensions.leftrec;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.util.Array;
import java.util.HashSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_LeftRecursionState_Inputs extends LeftRecursionState.Inputs {

  private final int position;
  private final Array<ParsingExpression> seeded;
  private final Array<ParseChanges> seeds;
  private final HashSet<ParsingExpression> blocked;

  AutoValue_LeftRecursionState_Inputs(
      int position,
      @com.norswap.util.annotations.Nullable Array<ParsingExpression> seeded,
      @com.norswap.util.annotations.Nullable Array<ParseChanges> seeds,
      HashSet<ParsingExpression> blocked) {
    this.position = position;
    this.seeded = seeded;
    this.seeds = seeds;
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
  HashSet<ParsingExpression> blocked() {
    return blocked;
  }

  @Override
  public String toString() {
    return "Inputs{"
        + "position=" + position + ", "
        + "seeded=" + seeded + ", "
        + "seeds=" + seeds + ", "
        + "blocked=" + blocked
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof LeftRecursionState.Inputs) {
      LeftRecursionState.Inputs that = (LeftRecursionState.Inputs) o;
      return (this.position == that.position())
           && ((this.seeded == null) ? (that.seeded() == null) : this.seeded.equals(that.seeded()))
           && ((this.seeds == null) ? (that.seeds() == null) : this.seeds.equals(that.seeds()))
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
    h ^= blocked.hashCode();
    return h;
  }

}
