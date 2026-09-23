# Testing notes — transparentactormodelchecker

Working document for the unit-test work on the Transparent Actor Model Checker.
It records what is covered, what the suite currently reports, which defects the
tests exposed, and what is planned next. Kept up to date as the work proceeds.

**Status:** 91 tests added over three rounds, taking the suite from 44 to 135; six
defects found and fixed. No test fails. One error remains, and it is a memory limit of
the machine rather than a defect: the four-philosopher model needs more heap than is
available here. 65 of the 79 classes in the package are now named by at least one test,
up from 55.
**Last updated:** 2026-08-31 · branch `tests/transparent-actor-coverage`

---

| | | |
|---|---|---|
| [1. Scope](#1-scope) | what is and is not touched | |
| [2. Building and running](#2-building-and-running) | the commands, in order | |
| [3. Baseline](#3-baseline-before-any-test-was-added) | 44 tests, 3 failures, 3 errors | |
| [4. What the tests cover](#4-what-the-tests-cover) | class by class | 8 classes |
| [5. Current results](#5-current-results) | how the suite moved | 135 / 0 / 1 |
| [6. Defects found](#6-defects-found) | symptom, cause, reproduction | 6 defects |
| [7. Changes to `src/main`](#7-changes-applied-to-srcmain) | what was edited and why | 6 + 1 |
| [8. Open questions](#8-open-questions) | for the advisor | 7 questions |
| [9. Still uncovered](#9-still-uncovered) | the honest gaps | 14 classes |
| [10. Log](#10-log) | dated record of each round | |

---

## 1. Scope

Work is confined to `org.rebecalang.transparentactormodelchecker`, the newer of the
two engines in this repository. The older `modelchecker` engine is untouched, and so
is `CoreRebecaModelsTest`, which is byte-identical to `master`.

Six small changes have been made to `src/main` plus one build setting, all listed in
section 7. Everything else is a test.

The work ran from Khordad to Shahrivar 1405 (June to September 2026), two stages per
month, in the order section 10 records: environment and baseline, then the scope and
rule tests, then the timed extension, then the six defects, then two further rounds of
tests driven by a coverage audit, then the write-up. The dates in section 10 are the
dates the commits landed, which is why they cluster at the end.

## 2. Building and running

The `org.rebecalang` artifacts are not published to Maven Central, and the poms pin
the sibling dependencies as `LATEST`, so the two dependencies must be installed into
the local repository first, in this order:

```
mvn -f org.rebecalang.compiler/pom.xml         install -DskipTests   # 2.31
mvn -f org.rebecalang.modeltransformer/pom.xml install -DskipTests   # 1.13
mvn -f org.rebecalang.modelchecker/pom.xml     test                  # 4.0
```

Requires JDK 17. The two siblings are used unmodified at `compiler` `dc663e0` and
`modeltransformer` `2a1d0bd`. `fb4197f4` is the `master` commit this work branched from;
the work itself is on `tests/transparent-actor-coverage`, head `8afc8e6`.

## 3. Baseline, before any test was added

Recorded on `master` so that later claims can be measured against something:

```
Tests run: 44, Failures: 3, Errors: 3, Skipped: 5
```

| Failure | Cause |
|---|---|
| `CoreRebecaModelsTest.GIVEN_RebecaModel_WHEN_No_Error` | `NullPointerException` in `ActivationRecord.hashCode` — it dereferences entry values unguarded while its own `equals` handles `null` |
| `CoreRebecaModelsTest.GIVEN_DiningPhilosopherModel...[3]`, `[4]` | `StackOverflowError` from unbounded recursion `dfs` → `deliverAllMessagesAndStore` → `dfs` |
| `TimedRebecaModelsTest.ticketService[1..3]` | state space collapses: expected 5 / 345 / 13723, got 3 / 5 / 7 |

The 5 skipped tests are the `@Disabled` legacy-engine tests.

## 4. What the tests cover

91 tests were added across eight classes; the last three are new files.

Coverage below is reported as *classes named by at least one test file*, counted by
searching every class name under `transparentactormodelchecker` in the text of the test
sources. It is a coarse measure — naming a class is not exercising it, so the number it
gives is an optimistic ceiling — but it is reproducible and it is what turned up the gap
the second and third rounds closed:

```
cd src
for f in $(find main -path '*transparentactormodelchecker*' -name '*.java'); do
  n=$(basename "$f" .java)
  grep -rq "$n" test || echo "no test names: $n"
done | wc -l          # 24 after round one, 14 now
```

| Test class | Before | After |
|---|---:|---:|
| `corerebeca/ActorScopeTest` | 2 | 19 |
| `corerebeca/StatementsSOSRulesTest` | 7 | 17 |
| `corerebeca/NetworkAndActorSOSRulesTest` | 2 | 10 |
| `timedrebeca/TimedNetworkTest` | 5 | 13 |
| `timedrebeca/TimedRebecaMessageTest` | 4 | 14 |
| `timedrebeca/TimeBucketTest` | 0 | 13 |
| `timedrebeca/TimedRuleTest` | 0 | 13 |
| `TransitionSystemStructureTest` | 0 | 12 |
| **suite total** | **44** | **135** |

### 4.1 `ActorScopeTest` — variable scoping

`ActorScope` has fourteen public methods and had two tests. The added tests cover:

- **Name lookup** — a bound name is found, an unbound one is not, and reading an
  unbound name raises `RebecaRuntimeInterpreterException`.
- **Scope stack** — a variable declared in an inner frame disappears when the frame
  is popped while the enclosing one survives; an inner frame shadows an outer name
  and the outer value returns after the pop.
- **Method-call frames** — a call frame jumps back to the actor's own frame, so the
  caller's block locals are not visible to the callee; `popToReturn` writes the
  return value into the named variable and discards any frames opened inside the call.
- **An actor as a variable of another actor** — an actor stored in scope is kept by
  id (`ActorStateRepresentor`) and resolved back through the environment's actor
  container, so reading it returns the same actor state. Nested access through the
  peer works for both a plain field (`peer.x`) and an array element (`peer.arr[1]`),
  for reading and for assignment, and an assignment lands in the peer's own scope.
- **Cloning and identity** — a clone is independent of the original; two scopes with
  equal frames are equal even when their environments differ, because the environment
  is shared between actors and must not take part in state identity.

One behaviour worth knowing: name lookup walks the scope stack down to index 0, which
holds the shared environment and is `null` in a freshly constructed `ActorScope`.
Looking up an absent name before an environment is attached therefore raises
`NullPointerException` rather than the intended interpreter exception.

### 4.2 `StatementsSOSRulesTest` — statement-level SOS rules

Six of the eleven statement-level rules had no test at all. All six are now covered:

| Rule | What the tests establish |
|---|---|
| `PushRule` | opens a new frame and advances the program counter |
| `PopRule` | pops exactly `numberOfPops` frames — verified for one and for two |
| `MethodCallRule` | enters the callee with its own program counter at the resolved body; a literal argument is bound as-is and a variable argument is evaluated in the caller |
| `EndMethodCallRule` | restores the caller's program counter, on the instruction after the call |
| `EndMSGSrvRule` | drops the message-server frame while the actor's own fields survive |
| `SendMessageRule` | addresses the named peer, falls back to self when no receiver is named, and carries an actor-valued argument by id rather than by reference |

`SendMessageRule` is the only statement-level rule without `@Component`, so it is not
a Spring bean and the test constructs it directly. `MethodCallRule` keeps its lookup
table in a plain field with a setter rather than an injected one, so the test sets it
explicitly; calling the rule without doing so fails with a bare `NullPointerException`.

### 4.3 `NetworkAndActorSOSRulesTest` — actor- and network-level rules

The existing two tests exercise the composition level. The added tests go one level
down, to the individual rules.

- `ReceiveMessageRule` places the message in the actor's queue and reports the same
  action it delivered.
- `CoreRebecaTakeMessageRule` prepares the message-server frame: the sender is bound,
  the message parameters enter scope, and the program counter is set to the start of
  the message server. It raises `RuleIsDisabledException` on an empty queue, and its
  enablement check refuses an actor that is already mid-execution.
- `NetworkReceiveMessageRule` groups messages by the sender/receiver route.
- `CoreRebecaNetworkLevelDeliverMessageRule` removes the delivered message from the
  network, is disabled on an empty network, and — the point worth recording —
  **produces one branch per route**, so delivery is non-deterministic and the checker
  explores each ordering.

`TakeMessageRule` and `NetworkLevelDeliverMessageRule` are abstract (the latter has no
members at all), so the tests target the concrete Core Rebeca subclasses.

### 4.4 `TimedRebecaMessageTest` and `TimedNetworkTest` — the timed extension

`TimedRebecaMessageState` and the actor's message bag:

- shift equivalence for an identical message (shift 0), for a later one, and for an
  earlier one (negative shift);
- non-equivalence when arrival and deadline move by different amounts, so the
  remaining slack differs, and when the receiver differs;
- cloning copies arrival and deadline and is independent of the original;
- the bag is ordered by arrival on receive, `bagIsEmpty` and `messageQueueIsEmpty`
  track its contents, and `getFirstMessageArrivalTime` reports the head;
- **only the first message of any given sender is enabled**, so a sender's stream
  keeps its order;
- `getEnableMessage` removes the message rather than peeking at it.

`TimedRebecaNetworkState`: messages sharing an arrival time share one time bucket,
distinct arrivals create separate buckets, two empty networks are shift-equivalent,
and networks with different bucket counts are not.

### 4.5 `TimeBucketTest` — the two bucket classes, driven directly

`TimeBucket` and `ActorReceivingBucket` are only ever reached through
`TimedRebecaNetworkState`, so 6.1 and 6.2 could sit in them for years without any test
failing. This class drives both directly:

- `TimeBucket.clone()` keeps the messages, produces copies rather than the same
  `ActorReceivingBucket`, and the clone can be extended without touching the original;
- messages for two receivers land in two separate receiving buckets;
- `shiftEquals` on two buckets holding the same message matches, and buckets with
  different receiver counts do not;
- `ActorReceivingBucket.shiftEquals` on two empty buckets matches; **two messages for
  one receiver that differ only in the second are not equivalent**, which is 6.2 as a
  direct assertion rather than an indirect one;
- a constant move of arrival and deadline is reported as that shift, and moving them by
  different amounts is not equivalent;
- cloning is independent, and `getAllSentMessages` keeps insertion order.

Every assertion in the class was checked by inversion, in three rounds because some
methods hold more than one.

### 4.6 `TimedRuleTest` — the timed SOS rules

The core-Rebeca take-message and delivery rules have tests; their timed counterparts,
which is what every Timed Rebeca model actually runs on, had none.

- `TimedRebecaTakeMessageRule`: disabled on an empty bag and on a message that has not
  arrived yet; an arrived message is taken, leaves the bag, and its parameters are bound
  into the new frame;
- `TimedRebecaFTTSNetworkLevelDeliverMessage`: disabled on an empty network; a held
  message is delivered and leaves the network; with messages at two instants **the
  earlier bucket goes first and the later one stays**;
- `TimedRebecaCompositionLevelTakeMessageRule`: disabled when no actor has an arrived
  message, and moves the one actor that does. Its `takeMessageRule` is injected, so the
  test sets it with `ReflectionTestUtils`;
- `TimedRebecaNetworkLevelDeliverMessage`: **its whole `applyRule` body is commented out
  in `src/main`**, so it can never produce a transition. One test records that, rather
  than leaving the class looking as though it works.

### 4.7 `TransitionSystemStructureTest` — the state space container

`CoreRebecaTransitionSystem.addIfNotExists` is what decides whether a newly reached state
is one the search has seen before, so every state count the tool reports is the number
this method arrives at. It had no test.

- a fresh transition system holds only its initial state;
- an unseen state is added and the size grows; the same state offered twice comes back as
  the existing object and the size does not grow; two different states both get added;
- an added state is linked to its predecessor in both directions.

One thing worth writing down, because it cost time here: the actors are **not** compared
by the commented-out `actorsContainer` branch of `AbstractSystemState.equals`. They are
compared through `environment`, which `setEnvironment` puts the container into. A test
that builds a system state without an environment finds every state equal to every other.

The class also covers `MethodCallActivationRecord` (the scope index takes part in equality,
and cloning is independent) and `ModelCheckingRuntimeException`.

## 5. Current results

```
129 tests outside CoreRebecaModelsTest : 0 failures, 0 errors, 5 skipped
CoreRebecaModelsTest                   : 5 of 6 pass, 1 out of memory
```

No test fails anywhere. The single remaining error is
`GIVEN_DiningPhilosopherModel[4]`, a model of 214107 states that exhausts the heap on
this machine — 7.7 GB total, 1.9 GB reachable by the JVM. It was already failing on
`master`.

How the suite moved:

| Stage | Tests | Failures | Errors |
|---|---:|---:|---:|
| `master`, before any work | 44 | 3 | 3 |
| after the tests were added | 97 | 6 | 3 |
| after the first three fixes | 97 | 3 | 3 |
| after the `delay` fix | 97 | 0 | 4 |
| after the null-hash fix and a larger test stack | 97 | 0 | **1** |
| after the bucket tests were added | **110** | 0 | **1** |
| after the timed rules and the transition system | **135** | 0 | **1** |

**A measurement limitation worth knowing.** When the four-philosopher model exhausts the
heap it kills the forked JVM, and surefire then reports nothing at all — not even for the
tests that had already passed in that fork. A single `mvn test` therefore produces no
totals on this machine. The numbers above come from two runs:

```
mvn test -Dtest='!CoreRebecaModelsTest'
mvn test -Dtest='CoreRebecaModelsTest#GIVEN_RebecaModel_WHEN_No_Error+GIVEN_SELF_LOOP_RebecaModel_WHEN_No_Error'
```

The three smaller Dining Philosophers cases were verified passing before change 6
in section 7; they cannot be re-measured after it, because the fourth case destroys the run before
any result is written. The print of each state-space size sits *after* its assertion, so
seeing `size: 105`, `size: 1471` and `size: 18053` in the log is itself proof that those
three assertions held.

Every one of the 91 added assertions was checked by inverting it and confirming that the
test then fails. A test that stays green when its assertion is reversed is not testing
anything. None survived.

The check runs in rounds, one assertion per test method per round, because when two
inversions land in the same method only the first is exercised — the first failing
assertion aborts the rest. The number of rounds is therefore the largest number of
assertions in any one method: three for `TimeBucketTest` and
`TransitionSystemStructureTest`, two for `TimedRuleTest`.

Two mechanical traps are worth knowing if you repeat this. The inverse of
`assertNotSame` is `assertSame`, which the file does not import, and the inverse of
`assertThrows(E.class, …)` is a different exception type. Get either wrong and the round
fails to *compile*, surefire reports nothing, and every assertion looks like a survivor.
That happened once here.

## 6. Defects found

Each was confirmed with a minimal failing test or a direct measurement before being
recorded here. The reproductions are kept as regression tests.

### 6.1 `TimeBucket.clone()` discarded every message

```java
TimeBucket timeBucket = new TimeBucket(time);
for (Entry<Integer, ActorReceivingBucket> arBucket : timeBucket.messages.entrySet()) {
```

The loop iterated `timeBucket.messages` — the newly created, empty map — instead of
`this.messages`, so its body never ran. The clone kept the right `time` and no messages.
`TimedRebecaNetworkState.clone()` calls this for every bucket, so cloning a timed network
state silently emptied it.

Reproduced by `GIVEN_ANetworkHoldingAMessage_WHEN_ItIsCloned_THEN_TheCloneStillHoldsThatMessage`
— `expected: <1> but was: <0>`.

### 6.2 `ActorReceivingBucket.shiftEquals()` compared only the first message

```java
for (int cnt = 0; cnt < this.sentMessages.size(); cnt++)
    thisMessages.get(cnt).shiftEquals(otherMessages.get(cnt));
```

The loop was bounded by `sentMessages.size()`, the number of receiver keys, while the
index was applied to `thisMessages`, the message list for one key. Every
`ActorReceivingBucket` is reached through `TimeBucket.getReceiverMessages(receiverId)`
and therefore holds messages for exactly one receiver, so the bound was always 1 and
every message after the first was never compared.

Reproduced by `GIVEN_AnActorReceivesTwoMessagesAtOneInstant_WHEN_OnlyTheSecondDiffers_THEN_TheNetworksAreNotEquivalent`
— `expected: <false> but was: <true>`.

### 6.3 `TimedRebecaNetworkState.addMessage()` did not keep buckets in time order

The `time > arrivalTime` case fell through both conditions and the loop continued, so it
behaved exactly like `time < arrivalTime`. The scan never stopped at the insertion point,
`cnt` always ended at `size()`, and `add(cnt, timeBucket)` degenerated into an append —
the `add(index, …)` call shows sorted insertion was intended. Bucket order then followed
insertion order, while `equals` and `shiftEquals` compare the two lists index by index.

Reproduced by `GIVEN_AMessageArrivesEarlierThanAnExistingOne_WHEN_ItIsAdded_THEN_BucketsStayOrderedByTime`
— `expected: <10> but was: <30>`.

### 6.4 The built-in `delay` method was registered under an unreachable name

This is the one that made `ticketService` report a collapsed state space.

`TransparentActorTimedRebecaFTTSModelChecker.initializeMethodBindingTable()` registers the
built-in `delay` through `RILUtilities.computeMethodName(null, "delay", …)`, and that
method builds its result as `className + "." + methodName`. With a `null` class name Java
renders the literal string `"null"`, so the entry went in as `null.delay$int`. The RIL
emits a base-less call as plain `delay$int`. Measured directly:

```
REGISTERED = [null.delay$int]
CALLED     = [delay$int]
```

`MethodLookup.resolveName` therefore returned `null`, `MethodCallRule` stored a program
counter of `(null, 0)`, and the next `getEnabledInstruction()` called
`RILModel.getInstructionList(null)`, which is a `Hashtable.get(null)` —
`NullPointerException`.

Every Timed Rebeca model that calls `delay` crashed the moment it reached that statement.
In the ticket service the crash happens in the third state, which is why the reported
state space was 3 instead of 5.

### 6.5 A crash inside model checking was reported as a result

```java
try {
    dfs(initialState);
} catch (Exception e) {
    e.printStackTrace();
    result = new TransparentActorModelCheckingResult(INTERNAL_ERROR);
    result.setTransitionSystem(transitionSystem);   // the partial one
    return result;
}
```

`modelcheck` catches everything, prints a stack trace, and returns the partially built
transition system. Callers that only read `getTransitionSystem().size()` — including
`TimedRebecaModelsTest` — get a plausible-looking number that is simply the point where
the search died.

This is worth separating from 6.4. The name-resolution bug was a crash; this is what
turned the crash into a wrong answer. Because of it, the symptom read as "the state
space collapses" and pointed the investigation at state merging, which cost a full round
of work before the real cause was measured. Fixed by change 6 in section 7, which narrows the catch rather than removing it.

### 6.6 `ActivationRecord.hashCode` dereferenced null values

`equals` in the same class treats a null value as a value in its own right, but
`hashCode` called `value.getClass()` on it. A single null-valued variable anywhere in
scope therefore broke hashing of the whole system state. This is the
`NullPointerException` that `CoreRebecaModelsTest.GIVEN_RebecaModel_WHEN_No_Error` had
been reporting since before this work started.

## 7. Changes applied to `src/main`

Six code changes and one build setting, each tied to a defect above.

| # | Change | Effect |
|---|---|---|
| 1 | `TimeBucket.clone()` — iterate `this.messages` | 6.1 reproduction green |
| 2 | `ActorReceivingBucket.shiftEquals()` — bound the loop by `thisMessages.size()` | 6.2 reproduction green |
| 3 | `TimedRebecaNetworkState.addMessage()` — stop at the insertion point, keep buckets in time order | 6.3 reproduction green |
| 4 | `TransparentActorTimedRebecaFTTSModelChecker` — register `delay` under the name the RIL actually calls | `ticketService` reaches its expected state counts |
| 5 | `ActivationRecord.hashCode()` — accept a null value, as `equals` already does | `GIVEN_RebecaModel_WHEN_No_Error` green |
| 6 | Both model checkers — narrow `catch (Exception)` to `catch (ModelCheckingException)` | a defect can no longer be returned as a result |
| 7 | `pom.xml` — run the tests with `-Xss64m` | two stack overflows cleared |

Fix 3 needed more than an added `break`. The check after the loop,
`if (cnt != receivedMessages.size())`, could not tell "found a bucket at the same time"
from "found the position to insert before". Breaking on `time > arrivalTime` alone would
have made it reuse a bucket belonging to a *later* time — worse than the original bug. A
flag now separates the two cases.

### What the first three fixes did to `ticketService`: nothing

6.2 was put forward as a plausible cause of the collapsing state space, on the grounds
that over-aggressive state merging produces exactly that shape. It was wrong. After
fixes 1–3 the numbers were byte-for-byte what they had been:

| case | expected | before | after fixes 1–3 |
|---|---:|---:|---:|
| [1] | 5 | 3 | 3 |
| [2] | 345 | 5 | 5 |
| [3] | 13723 | 7 | 7 |

Not a single state moved. That ruled the hypothesis out and sent the investigation back
to measurement rather than reading: dumping the three-state transition system showed the
search stopping precisely before `TicketService.requestTicket`, whose body is the only
place the model calls `delay`. Printing the registered name against the called name gave
6.4 directly.

### What fix 4 did

| case | expected | before | after |
|---|---:|---:|---:|
| [1] | 5 | 3 | **5** |
| [2] | 345 | 5 | **345** |
| [3] | 13723 | 7 | `StackOverflowError` |

The first two now reach their expected counts exactly. The third explores far enough to
exhaust the JVM stack in the recursive `dfs` / `deliverAllMessagesAndExpand` pair.

That third result is progress, not a regression. Before the fix the model was never
explored at all: the search died after three states and returned 7 as though it were an
answer. It now runs until it genuinely runs out of stack — a visible failure instead of a
quiet wrong number.

### What fixes 5 to 7 did

Fix 5 cleared the `NullPointerException` that had been failing
`GIVEN_RebecaModel_WHEN_No_Error` since before this work began.

Fix 6 is the one behind 6.5, and it is deliberately narrow. `dfs` signals the ordinary
end of a search — a state where no rule applies — by throwing `ModelCheckingException`,
so that path still has to be caught and turned into a result. What the old
`catch (Exception)` also caught was every genuine defect, which is how a crash came back
looking like an answer. Catching only `ModelCheckingException` keeps the ordinary path
exactly as it was and lets a real fault reach the caller. Had this been in place from the
start, `ticketService` would have reported a `NullPointerException` instead of "expected
5 but was 3", and 6.4 would have been found immediately instead of after a round of work
spent on the wrong hypothesis.

Fix 7 is a build setting, not a code change. The search recurses once per explored state,
so the larger models need more than the default thread stack. This cleared the stack
overflows on `ticketService[3]` (13723 states) and `GIVEN_DiningPhilosopherModel[3]`
(18053 states); both now reach their expected counts.

Rewriting `dfs` iteratively would remove the limit properly rather than raising it, but
that is a change to the algorithm itself and belongs with whoever owns its design.

### What is left

| Error | Note |
|---|---|
| `CoreRebecaModelsTest.GIVEN_DiningPhilosopherModel[4]` | 214107 states; exhausts the heap on this machine, and was already failing on `master` |

Nothing else fails or errors. This one is not a defect: 18053 states fit in the 1.9 GB the
JVM can reach here, and 214107 states need roughly an order of magnitude more than the
machine has in total. It needs either more memory or a smaller per-state footprint.

## 8. Open questions

- Should the recursive `dfs` be rewritten with an explicit work list? Raising the stack
  clears the cases seen here, but the limit is still there.
- Would `RILUtilities.computeMethodName` be better off treating a `null` class name as
  "no class" rather than rendering the string `"null"`? That is the upstream form of 6.4
  and would close the trap for every future caller, but it lives in
  `org.rebecalang.modeltransformer` and affects everything that calls it.
- In `TimedRebecaActorState.receiveMessage`, messages arriving at the same instant are
  ordered by *descending* name. Any deterministic order is sound, so this may well be
  intentional.
- `SendMessageRule` lacks `@Component` while the other ten statement-level rules have it:
  deliberate because `TimedRebecaSendMessageSOSRule` extends it, or an oversight?
- Should `ActorScope` name lookup tolerate a missing environment instead of raising
  `NullPointerException`?
- `TimedRebecaNetworkLevelDeliverMessage.applyRule` has its whole body commented out, so
  it can only ever throw `RuleIsDisabledException`. Is the class still wanted, or was the
  FTTS variant meant to replace it? Six other classes in the package have no caller at
  all. None of this produces wrong answers, so it is not counted among the six defects,
  but it is the kind of thing that makes the engine look larger than it is.
- The `actorsContainer` branch of `AbstractSystemState.equals` and `hashCode` is commented
  out. It looks alarming and is not: the actors reach the comparison through
  `environment`, which `setEnvironment` puts the container into. Worth a comment in the
  code so the next reader does not spend the time this cost.

## 9. Still uncovered

14 of the 79 classes under `transparentactormodelchecker` are still not named by any
test. They fall into three groups, and only the last is worth work:

- **abstract, covered through their subclasses** — `Action`, `AbstractSOSRule`,
  `TakeMessageRule`, `NetworkLevelDeliverMessageRule`, `TransparentActorTransitionSystem`,
  `TransparentActorAbstractModelChecker`;
- **dead code with no caller anywhere in `src/main`** — `AbstractTransition`,
  `DeterministicTransition` and `NondeterministicTransition` (all three `@Deprecated` and
  superseded by `Transition`), plus `NetworkDeliveryAction`, `ShiftTimeAction`,
  `RebecaStateSerializationUtil` and `TransparentActorModelChecker`. A test here would
  assert things about code nothing runs; deleting them would be the real fix;
- **reachable but needing a compiled model** — `StateGenerationUtils.getEnvironment`
  takes a `RebecaModel`, so testing it means compiling a model first.

Still thin rather than absent: `TimedRebecaActorState.memoizedClone`, `isEnable` and
`createNewActorState`, and `TimedActorScope`.

The five `@Disabled` tests were left alone on purpose. All of them are under
`org/rebecalang/modelchecker`, the older engine, which is outside the area this work was
asked to cover. Enabling them would mean taking on whatever they turn up in a part of the
codebase nobody asked to touch.

## 10. Log

| Date | Change |
|---|---|
| 2026-08-30 | Baseline recorded (44 tests, 3 failures, 3 errors). 53 tests added across five classes; suite at 97. Three defects found and reproduced. `src/main` unchanged. |
| 2026-08-30 | The three defects fixed; their reproductions pass and the suite is at 97 / 3 / 3. `ticketService` unchanged, ruling 6.2 out as its cause. |
| 2026-08-30 | `ticketService` root-caused to 6.4 by measurement and fixed. Suite at 97 / 0 / 4: all assertions pass, and the remaining errors are crashes on large models, three of which predate this work. |
| 2026-08-30 | Null-value hashing fixed (6.6), the catch in both model checkers narrowed (6.5), and the tests given a larger stack. Nothing fails now; the only error left is the four-philosopher model running out of heap on this machine. |
| 2026-08-30 | `TimeBucketTest` added: 13 tests driving `TimeBucket` and `ActorReceivingBucket` directly, the two classes 6.1 and 6.2 lived in and which until now had no test of their own. Suite at 110 / 0 / 1. |
| 2026-08-31 | Document revised: contents table, the coverage command made reproducible, the inversion procedure and its two compile traps written down, and an ambiguous cross-reference to section 7 fixed. No code change. |
| 2026-08-30 | `TimedRuleTest` (13) and `TransitionSystemStructureTest` (12) added, covering the timed SOS rules and the state space container. Suite at 135 / 0 / 1; 65 of the 79 engine classes are now named by a test, up from 55 at the baseline. |
