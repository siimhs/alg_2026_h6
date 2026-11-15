/*
 * These tests were written by Claude Opus 5.5 (Anthropic), an AI assistant,
 * at the request of the student.
 */

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

/** Tests for {@link GraphTask.Graph#findFewestTransfersRoute}.
 * @author jaanus
 */
public class GraphTaskTest {

   private final GraphTask task = new GraphTask();

   /** Run the demo main method. */
   @Test (timeout=20000)
   public void testMain() {
      GraphTask.main (null);
   }

   /** Fewer transfers win over fewer arcs. */
   @Test (timeout=1000)
   public void testPrefersFewerTransfers() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex b = g.createVertex ("B");
      GraphTask.Vertex c = g.createVertex ("C");
      GraphTask.Vertex d = g.createVertex ("D");
      GraphTask.Vertex e = g.createVertex ("E");
      g.createArc ("ab", a, b, "1");
      g.createArc ("be", b, e, "2");
      GraphTask.Arc ac = g.createArc ("ac", a, c, "3");
      GraphTask.Arc cd = g.createArc ("cd", c, d, "3");
      GraphTask.Arc de = g.createArc ("de", d, e, "3");
      List<GraphTask.Arc> r = g.findFewestTransfersRoute (a, e);
      assertEquals (Arrays.asList (ac, cd, de), r);
      assertEquals (0, GraphTask.countTransfers (r));
   }

   /** With equal transfers, fewer arcs win. */
   @Test (timeout=1000)
   public void testTieBrokenByArcCount() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex b = g.createVertex ("B");
      GraphTask.Vertex c = g.createVertex ("C");
      GraphTask.Vertex d = g.createVertex ("D");
      GraphTask.Vertex t = g.createVertex ("T");
      g.createArc ("ab", a, b, "1");
      g.createArc ("bc", b, c, "1");
      g.createArc ("ct", c, t, "2");
      GraphTask.Arc ad = g.createArc ("ad", a, d, "3");
      GraphTask.Arc dt = g.createArc ("dt", d, t, "4");
      assertEquals (Arrays.asList (ad, dt), g.findFewestTransfersRoute (a, t));
   }

   /** Parallel arcs: the one continuing the current line is chosen. */
   @Test (timeout=1000)
   public void testParallelArcs() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex b = g.createVertex ("B");
      GraphTask.Vertex c = g.createVertex ("C");
      g.createArc ("ab1", a, b, "1");
      GraphTask.Arc ab2 = g.createArc ("ab2", a, b, "2");
      g.createArc ("bc3", b, c, "3");
      GraphTask.Arc bc2 = g.createArc ("bc2", b, c, "2");
      assertEquals (Arrays.asList (ab2, bc2), g.findFewestTransfersRoute (a, c));
   }

   /**
    * The cheapest way to reach an intermediate stop is not the right one:
    * B is reached in 1 arc on line 1, but arriving on line 2 (2 arcs)
    * allows continuing to T without a transfer. A search that keeps only
    * one label per stop would fail here.
    */
   @Test (timeout=1000)
   public void testArrivingLineMatters() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex x = g.createVertex ("X");
      GraphTask.Vertex b = g.createVertex ("B");
      GraphTask.Vertex t = g.createVertex ("T");
      g.createArc ("ab", a, b, "1");
      GraphTask.Arc ax = g.createArc ("ax", a, x, "2");
      GraphTask.Arc xb = g.createArc ("xb", x, b, "2");
      GraphTask.Arc bt = g.createArc ("bt", b, t, "2");
      List<GraphTask.Arc> r = g.findFewestTransfersRoute (a, t);
      assertEquals (Arrays.asList (ax, xb, bt), r);
      assertEquals (0, GraphTask.countTransfers (r));
   }

   /** Arc direction is respected: unreachable target gives empty list. */
   @Test (timeout=1000)
   public void testUnreachable() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex b = g.createVertex ("B");
      GraphTask.Vertex c = g.createVertex ("C");
      g.createArc ("ab", a, b, "1");
      g.createArc ("cb", c, b, "1");
      assertTrue (g.findFewestTransfersRoute (a, c).isEmpty());
      assertTrue (g.findFewestTransfersRoute (b, a).isEmpty());
   }

   /** Isolated stops: empty list. */
   @Test (timeout=1000)
   public void testNoArcs() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex b = g.createVertex ("B");
      assertTrue (g.findFewestTransfersRoute (a, b).isEmpty());
   }

   /** Source equals target: empty route. */
   @Test (timeout=1000)
   public void testSameStop() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      GraphTask.Vertex b = g.createVertex ("B");
      g.createArc ("ab", a, b, "1");
      g.createArc ("ba", b, a, "1");
      assertTrue (g.findFewestTransfersRoute (a, a).isEmpty());
   }

   /** Null arguments are rejected. */
   @Test (expected = IllegalArgumentException.class)
   public void testNullSource() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      g.findFewestTransfersRoute (null, a);
   }

   /** Null arguments are rejected. */
   @Test (expected = IllegalArgumentException.class)
   public void testNullTarget() {
      GraphTask.Graph g = task.new Graph ("G");
      GraphTask.Vertex a = g.createVertex ("A");
      g.findFewestTransfersRoute (a, null);
   }

   /** Searching does not modify the graph. */
   @Test (timeout=5000)
   public void testGraphUnchanged() {
      GraphTask.Graph g = task.new Graph ("G");
      g.createRandomTransitGraph (50, 15, 10, new Random (7));
      String before = g.toString();
      List<GraphTask.Vertex> vs = g.getVertices();
      for (GraphTask.Vertex from : vs)
         g.findFewestTransfersRoute (from, vs.get (0));
      assertEquals (before, g.toString());
   }

   /**
    * Compare with exhaustive search over all simple paths on many small
    * random graphs. (An optimal route never needs to revisit a stop:
    * cutting out a cycle does not add transfers and removes arcs.)
    */
   @Test (timeout=20000)
   public void testAgainstBruteForce() {
      for (int seed = 0; seed < 400; seed++) {
         Random rnd = new Random (seed);
         int n = 2 + rnd.nextInt (6);
         GraphTask.Graph g = task.new Graph ("R" + seed);
         g.createRandomTransitGraph (n, 1 + rnd.nextInt (5),
            2 + rnd.nextInt (5), rnd);
         List<GraphTask.Vertex> vs = g.getVertices();
         for (GraphTask.Vertex from : vs) {
            for (GraphTask.Vertex to : vs) {
               if (from == to)
                  continue;
               List<GraphTask.Arc> r = g.findFewestTransfersRoute (from, to);
               int[] opt = bruteForce (from, to);
               String msg = "seed " + seed + " " + from + "->" + to
                  + " route " + r;
               if (opt == null) {
                  assertTrue (msg, r.isEmpty());
               } else {
                  assertValidRoute (msg, from, to, r);
                  assertEquals (msg, opt [0], GraphTask.countTransfers (r));
                  assertEquals (msg, opt [1], r.size());
               }
            }
         }
      }
   }

   /** Large graph (2000+ stops) finishes quickly and gives a valid route. */
   @Test (timeout=10000)
   public void testLargeGraph() {
      GraphTask.Graph g = task.new Graph ("Large");
      g.createRandomTransitGraph (3000, 400, 40, new Random (1));
      List<GraphTask.Vertex> vs = g.getVertices();
      GraphTask.Vertex from = vs.get (0);
      GraphTask.Vertex to = vs.get (vs.size() - 1);
      long start = System.nanoTime();
      List<GraphTask.Arc> r = g.findFewestTransfersRoute (from, to);
      long ms = (System.nanoTime() - start) / 1_000_000;
      System.out.println ("testLargeGraph: " + g.arcCount() + " arcs, "
         + r.size() + " arcs in route, "
         + GraphTask.countTransfers (r) + " transfers, " + ms + " ms");
      if (!r.isEmpty())
         assertValidRoute ("large", from, to, r);
   }

   /**
    * Check that the arcs form a connected route from {@code from} to
    * {@code to}.
    */
   private static void assertValidRoute (String msg, GraphTask.Vertex from,
         GraphTask.Vertex to, List<GraphTask.Arc> r) {
      assertFalse (msg, r.isEmpty());
      GraphTask.Vertex cur = from;
      for (GraphTask.Arc a : r) {
         assertTrue (msg + ": arc " + a + " does not leave " + cur,
            cur.getArcs().contains (a));
         cur = a.getTarget();
      }
      assertSame (msg, to, cur);
   }

   /**
    * Exhaustive search over all simple paths.
    * @return {transfers, arcs} of the best route, or null if unreachable
    */
   private static int[] bruteForce (GraphTask.Vertex from,
                                    GraphTask.Vertex to) {
      int[][] best = new int[1][];
      Set<GraphTask.Vertex> visited = new HashSet<GraphTask.Vertex>();
      visited.add (from);
      dfs (from, to, null, 0, 0, visited, best);
      return best [0];
   }

   private static void dfs (GraphTask.Vertex v, GraphTask.Vertex to,
         GraphTask.Arc last, int transfers, int arcs,
         Set<GraphTask.Vertex> visited, int[][] best) {
      if (v == to) {
         if (best [0] == null || transfers < best [0][0]
               || (transfers == best [0][0] && arcs < best [0][1]))
            best [0] = new int[] {transfers, arcs};
         return;
      }
      for (GraphTask.Arc a : v.getArcs()) {
         GraphTask.Vertex w = a.getTarget();
         if (visited.contains (w))
            continue;
         int t = transfers;
         if (last != null && !Objects.equals (last.getLine(), a.getLine()))
            t++;
         visited.add (w);
         dfs (w, to, a, t, arcs + 1, visited, best);
         visited.remove (w);
      }
   }
}
