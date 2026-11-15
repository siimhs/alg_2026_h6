/*
 * The solution (findFewestTransfersRoute and its helpers, the line label
 * of arcs, examples and javadoc) was written by Claude Opus 5.5 (Anthropic),
 * an AI assistant, at the request of the student. The class skeleton
 * (Graph, Vertex, Arc and the random graph generators) is from the course
 * template.
 *
 * Ülesanne:
 * On antud suunatud graaf, mille tipud tähistavad peatusi ja kaared
 * otseühendusi kahe peatuse vahel.
 * Iga kaare infoväljas on liini tähis, mis seda ühendust teenindab (kahe
 * peatuse vahel võib olla mitu kaart eri liinidega).
 * Koostada meetod, mis leiab etteantud lähtepeatusest sihtpeatusesse
 * marsruudi, kus liini vahetusi (ümberistumisi) on võimalikult vähe.
 * Kui selliseid marsruute on mitu, valitakse neist kõige vähemate kaartega.
 * Meetod tagastab marsruudi kaarte listina (List) läbimise järjekorras.
 * Kui sihtpeatusesse ei saa jõuda, tagastatakse tühi list.
 * Algset graafi ei muudeta.
 */

import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally.
 * <p>
 * Problem: the directed graph represents a public transport network.
 * Vertices are stops and arcs are direct connections between two stops.
 * Every arc carries the label of the line serving that connection (there
 * may be several arcs with different lines between the same two stops).
 * The task is to find a route from a given source stop to a given target
 * stop with the minimal number of line changes (transfers); among such
 * routes the one with the fewest arcs is chosen. The route is returned as
 * a list of arcs in travelling order, an empty list if the target cannot
 * be reached. The original graph is not modified.
 * </p>
 */
public class GraphTask {

   /** Main method.
    * @param args command line arguments (not used)
    */
   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   /** Actual main method to run examples and everything. */
   public void run() {
      example1();
      example2();
      example3();
      example4();
      example5();
      exampleLarge (2500, 300, 30);
   }

   /**
    * Example 1: the shortest route (2 arcs) needs a transfer, a longer
    * route (3 arcs) on a single line needs none and must be chosen.
    */
   private void example1() {
      Graph g = new Graph ("E1");
      Vertex a = g.createVertex ("A");
      Vertex b = g.createVertex ("B");
      Vertex c = g.createVertex ("C");
      Vertex d = g.createVertex ("D");
      Vertex e = g.createVertex ("E");
      g.createArc ("aA_B_1", a, b, "1");
      g.createArc ("aB_E_2", b, e, "2");
      g.createArc ("aA_C_3", a, c, "3");
      g.createArc ("aC_D_3", c, d, "3");
      g.createArc ("aD_E_3", d, e, "3");
      printResult (g, a, e);
   }

   /**
    * Example 2: two routes with one transfer each; the one with fewer
    * arcs must be chosen.
    */
   private void example2() {
      Graph g = new Graph ("E2");
      Vertex a = g.createVertex ("A");
      Vertex b = g.createVertex ("B");
      Vertex c = g.createVertex ("C");
      Vertex d = g.createVertex ("D");
      Vertex t = g.createVertex ("T");
      g.createArc ("aA_B_1", a, b, "1");
      g.createArc ("aB_C_1", b, c, "1");
      g.createArc ("aC_T_2", c, t, "2");
      g.createArc ("aA_D_3", a, d, "3");
      g.createArc ("aD_T_4", d, t, "4");
      printResult (g, a, t);
   }

   /**
    * Example 3: parallel arcs with different lines between the same
    * stops; the right parallel arc must be picked to avoid transfers.
    */
   private void example3() {
      Graph g = new Graph ("E3");
      Vertex a = g.createVertex ("A");
      Vertex b = g.createVertex ("B");
      Vertex c = g.createVertex ("C");
      Vertex d = g.createVertex ("D");
      g.createArc ("aA_B_1", a, b, "1");
      g.createArc ("aA_B_2", a, b, "2");
      g.createArc ("aB_C_2", b, c, "2");
      g.createArc ("aB_C_3", b, c, "3");
      g.createArc ("aC_D_3", c, d, "3");
      g.createArc ("aC_D_2", c, d, "2");
      printResult (g, a, d);
   }

   /** Example 4: the target is unreachable (arcs point the wrong way). */
   private void example4() {
      Graph g = new Graph ("E4");
      Vertex a = g.createVertex ("A");
      Vertex b = g.createVertex ("B");
      Vertex c = g.createVertex ("C");
      g.createArc ("aA_B_1", a, b, "1");
      g.createArc ("aC_B_1", c, b, "1");
      printResult (g, a, c);
   }

   /** Example 5: a random small transit network. */
   private void example5() {
      Graph g = new Graph ("E5");
      g.createRandomTransitGraph (8, 4, 5, new Random (5));
      List<Vertex> vs = g.getVertices();
      printResult (g, vs.get (0), vs.get (vs.size() - 1));
   }

   /**
    * Large example: only measure the running time.
    * @param n number of stops
    * @param lines number of lines
    * @param lineLength number of stops on each line
    */
   private void exampleLarge (int n, int lines, int lineLength) {
      Graph g = new Graph ("Large");
      g.createRandomTransitGraph (n, lines, lineLength, new Random (42));
      List<Vertex> vs = g.getVertices();
      long start = System.nanoTime();
      List<Arc> route = g.findFewestTransfersRoute (vs.get (0),
         vs.get (vs.size() - 1));
      long ms = (System.nanoTime() - start) / 1_000_000;
      System.out.println ("Large graph: " + n + " stops, " + g.arcCount()
         + " arcs, route of " + route.size() + " arcs with "
         + countTransfers (route) + " transfers found in " + ms + " ms");
   }

   /**
    * Print graph, query and the found route.
    * @param g graph
    * @param from source stop
    * @param to target stop
    */
   private void printResult (Graph g, Vertex from, Vertex to) {
      System.out.println (g);
      List<Arc> route = g.findFewestTransfersRoute (from, to);
      System.out.println ("Route " + from + " -> " + to + ": " + route
         + (route.isEmpty() ? " (none)" : "")
         + ", arcs: " + route.size()
         + ", transfers: " + countTransfers (route));
   }

   /**
    * Count line changes on a route: the number of consecutive arc pairs
    * whose lines differ.
    * @param route list of arcs in travelling order
    * @return number of transfers (0 for an empty route)
    */
   public static int countTransfers (List<Arc> route) {
      int res = 0;
      for (int i = 1; i < route.size(); i++) {
         if (!Objects.equals (route.get (i - 1).line, route.get (i).line))
            res++;
      }
      return res;
   }

   /** Vertex represents one stop in the transit network. Outgoing arcs
    * of the vertex form a singly linked list starting from {@code first}.
    */
   class Vertex {

      private String id;
      private Vertex next;
      private Arc first;
      private int info = 0;

      /**
       * Create a vertex.
       * @param s identifier
       * @param v next vertex in the graph's vertex list
       * @param e first outgoing arc
       */
      Vertex (String s, Vertex v, Arc e) {
         id = s;
         next = v;
         first = e;
      }

      /**
       * Create a vertex without neighbours.
       * @param s identifier
       */
      Vertex (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      /**
       * List of outgoing arcs of this vertex.
       * @return new list of outgoing arcs
       */
      public List<Arc> getArcs() {
         List<Arc> res = new ArrayList<Arc>();
         for (Arc a = first; a != null; a = a.next)
            res.add (a);
         return res;
      }
   }


   /** Arc represents one arrow in the graph: a direct connection from one
    * stop to another, served by the line {@code line}. Two-directional
    * edges are represented by two Arc objects (for both directions).
    */
   class Arc {

      private String id;
      private Vertex target;
      private Arc next;
      private int info = 0;
      /** Label of the line serving this connection. */
      private String line;

      /**
       * Create an arc.
       * @param s identifier
       * @param v target vertex
       * @param a next arc in the source vertex's arc list
       */
      Arc (String s, Vertex v, Arc a) {
         id = s;
         target = v;
         next = a;
      }

      /**
       * Create an arc without target.
       * @param s identifier
       */
      Arc (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }

      /**
       * Target vertex of this arc.
       * @return target vertex
       */
      public Vertex getTarget() {
         return target;
      }

      /**
       * Line label of this arc.
       * @return line label (may be null if not set)
       */
      public String getLine() {
         return line;
      }
   }


   /** Graph represents the transit network as a linked list of vertices,
    * each with its own linked list of outgoing arcs.
    */
   class Graph {

      private String id;
      private Vertex first;
      private int info = 0;

      /**
       * Create a graph.
       * @param s identifier
       * @param v first vertex
       */
      Graph (String s, Vertex v) {
         id = s;
         first = v;
      }

      /**
       * Create an empty graph.
       * @param s identifier
       */
      Graph (String s) {
         this (s, null);
      }

      @Override
      public String toString() {
         String nl = System.getProperty ("line.separator");
         StringBuffer sb = new StringBuffer (nl);
         sb.append (id);
         sb.append (nl);
         Vertex v = first;
         while (v != null) {
            sb.append (v.toString());
            sb.append (" -->");
            Arc a = v.first;
            while (a != null) {
               sb.append (" ");
               sb.append (a.toString());
               sb.append (" (");
               sb.append (v.toString());
               sb.append ("->");
               sb.append (a.target.toString());
               if (a.line != null) {
                  sb.append (", line ");
                  sb.append (a.line);
               }
               sb.append (")");
               a = a.next;
            }
            sb.append (nl);
            v = v.next;
         }
         return sb.toString();
      }

      /**
       * Create a new vertex and add it to the graph.
       * @param vid vertex identifier
       * @return new vertex
       */
      public Vertex createVertex (String vid) {
         Vertex res = new Vertex (vid);
         res.next = first;
         first = res;
         return res;
      }

      /**
       * Create a new arc without a line label.
       * @param aid arc identifier
       * @param from source vertex
       * @param to target vertex
       * @return new arc
       */
      public Arc createArc (String aid, Vertex from, Vertex to) {
         Arc res = new Arc (aid);
         res.next = from.first;
         from.first = res;
         res.target = to;
         return res;
      }

      /**
       * Create a new arc served by the given line.
       * @param aid arc identifier
       * @param from source stop
       * @param to target stop
       * @param line line label
       * @return new arc
       */
      public Arc createArc (String aid, Vertex from, Vertex to, String line) {
         Arc res = createArc (aid, from, to);
         res.line = line;
         return res;
      }

      /**
       * List of vertices in the order of the internal vertex list.
       * @return new list of vertices
       */
      public List<Vertex> getVertices() {
         List<Vertex> res = new ArrayList<Vertex>();
         for (Vertex v = first; v != null; v = v.next)
            res.add (v);
         return res;
      }

      /**
       * Number of arcs in the graph.
       * @return arc count
       */
      public int arcCount() {
         int res = 0;
         for (Vertex v = first; v != null; v = v.next)
            for (Arc a = v.first; a != null; a = a.next)
               res++;
         return res;
      }

      /**
       * Create a connected undirected random tree with n vertices.
       * Each new vertex is connected to some random existing vertex.
       * @param n number of vertices added to this graph
       */
      public void createRandomTree (int n) {
         if (n <= 0)
            return;
         Vertex[] varray = new Vertex [n];
         for (int i = 0; i < n; i++) {
            varray [i] = createVertex ("v" + String.valueOf(n-i));
            if (i > 0) {
               int vnr = (int)(Math.random()*i);
               createArc ("a" + varray [vnr].toString() + "_"
                  + varray [i].toString(), varray [vnr], varray [i]);
               createArc ("a" + varray [i].toString() + "_"
                  + varray [vnr].toString(), varray [i], varray [vnr]);
            } else {}
         }
      }

      /**
       * Create an adjacency matrix of this graph.
       * Side effect: corrupts info fields in the graph
       * @return adjacency matrix
       */
      public int[][] createAdjMatrix() {
         info = 0;
         Vertex v = first;
         while (v != null) {
            v.info = info++;
            v = v.next;
         }
         int[][] res = new int [info][info];
         v = first;
         while (v != null) {
            int i = v.info;
            Arc a = v.first;
            while (a != null) {
               int j = a.target.info;
               res [i][j]++;
               a = a.next;
            }
            v = v.next;
         }
         return res;
      }

      /**
       * Create a connected simple (undirected, no loops, no multiple
       * arcs) random graph with n vertices and m edges.
       * @param n number of vertices
       * @param m number of edges
       */
      public void createRandomSimpleGraph (int n, int m) {
         if (n <= 0)
            return;
         if (n > 2500)
            throw new IllegalArgumentException ("Too many vertices: " + n);
         if (m < n-1 || m > n*(n-1)/2)
            throw new IllegalArgumentException
               ("Impossible number of edges: " + m);
         first = null;
         createRandomTree (n);       // n-1 edges created here
         Vertex[] vert = new Vertex [n];
         Vertex v = first;
         int c = 0;
         while (v != null) {
            vert[c++] = v;
            v = v.next;
         }
         int[][] connected = createAdjMatrix();
         int edgeCount = m - n + 1;  // remaining edges
         while (edgeCount > 0) {
            int i = (int)(Math.random()*n);  // random source
            int j = (int)(Math.random()*n);  // random target
            if (i==j)
               continue;  // no loops
            if (connected [i][j] != 0 || connected [j][i] != 0)
               continue;  // no multiple edges
            Vertex vi = vert [i];
            Vertex vj = vert [j];
            createArc ("a" + vi.toString() + "_" + vj.toString(), vi, vj);
            connected [i][j] = 1;
            createArc ("a" + vj.toString() + "_" + vi.toString(), vj, vi);
            connected [j][i] = 1;
            edgeCount--;  // a new edge happily created
         }
      }

      /**
       * Create a random directed transit network. The graph is cleared,
       * {@code n} stops are created, then {@code lines} lines are generated,
       * each as a random walk visiting {@code lineLength} stops (consecutive
       * stops are distinct). Every step of a line becomes an arc labelled
       * with that line, so parallel arcs of different lines may appear.
       * @param n number of stops
       * @param lines number of lines
       * @param lineLength number of stops on each line (at least 2)
       * @param rnd random number generator
       */
      public void createRandomTransitGraph (int n, int lines, int lineLength,
                                            Random rnd) {
         if (n < 2)
            throw new IllegalArgumentException ("Too few stops: " + n);
         if (lineLength < 2)
            throw new IllegalArgumentException
               ("Line must have at least 2 stops: " + lineLength);
         first = null;
         Vertex[] vert = new Vertex [n];
         for (int i = n - 1; i >= 0; i--)
            vert [i] = createVertex ("v" + (i + 1));
         for (int l = 1; l <= lines; l++) {
            String line = "L" + l;
            int cur = rnd.nextInt (n);
            for (int s = 1; s < lineLength; s++) {
               int nxt = rnd.nextInt (n - 1);
               if (nxt >= cur)
                  nxt++;            // skip the current stop: no loops
               createArc ("a" + vert [cur] + "_" + vert [nxt] + "_" + line,
                  vert [cur], vert [nxt], line);
               cur = nxt;
            }
         }
      }

      /**
       * Find a route from {@code from} to {@code to} with the minimal
       * number of line changes (transfers); among routes with equally few
       * transfers the one with the fewest arcs is chosen.
       * <p>
       * A transfer is counted whenever two consecutive arcs of the route
       * have different line labels; boarding the first arc is not a
       * transfer. The search is Dijkstra's algorithm over states
       * (stop, line of the arc used to arrive there) with the cost pair
       * (transfers, arcs) compared lexicographically. Taking an arc of
       * the same line as the arriving arc costs (0, 1), of another line
       * (1, 1). Tracking the arriving line is necessary because reaching
       * a stop more cheaply on one line can be worse later than reaching
       * it on another line. There are at most E + 1 states, every state
       * scans the outgoing arcs of its stop, giving
       * O(E * d * log E) time, where d is the maximal out-degree.
       * </p>
       * <p>
       * The graph is not modified: all auxiliary data is kept in local
       * maps, info fields are not used.
       * </p>
       * @param from source stop
       * @param to target stop
       * @return route as a list of arcs in travelling order; an empty list
       *    if the target cannot be reached or {@code from == to}
       * @throws IllegalArgumentException if {@code from} or {@code to}
       *    is null
       */
      public List<Arc> findFewestTransfersRoute (Vertex from, Vertex to) {
         if (from == null || to == null)
            throw new IllegalArgumentException
               ("Source and target stops must not be null");
         List<Arc> route = new ArrayList<Arc>();
         if (from == to)
            return route;

         State start = new State (from, null, true);
         Map<State, int[]> best = new HashMap<State, int[]>();
         Map<State, Arc> viaArc = new HashMap<State, Arc>();
         Map<State, State> prev = new HashMap<State, State>();
         PriorityQueue<QueueEntry> queue = new PriorityQueue<QueueEntry>();
         best.put (start, new int[] {0, 0});
         queue.add (new QueueEntry (start, 0, 0));

         State found = null;
         while (!queue.isEmpty()) {
            QueueEntry e = queue.poll();
            int[] cur = best.get (e.state);
            if (cur [0] != e.transfers || cur [1] != e.arcs)
               continue;            // stale entry, a better one was used
            if (e.state.stop == to) {
               found = e.state;
               break;
            }
            for (Arc a = e.state.stop.first; a != null; a = a.next) {
               int t = e.transfers;
               if (!e.state.start && !Objects.equals (e.state.line, a.line))
                  t++;
               int n = e.arcs + 1;
               State s = new State (a.target, a.line, false);
               int[] old = best.get (s);
               if (old == null || t < old [0]
                     || (t == old [0] && n < old [1])) {
                  best.put (s, new int[] {t, n});
                  viaArc.put (s, a);
                  prev.put (s, e.state);
                  queue.add (new QueueEntry (s, t, n));
               }
            }
         }
         if (found == null)
            return route;
         for (State s = found; !s.start; s = prev.get (s))
            route.add (viaArc.get (s));
         Collections.reverse (route);
         return route;
      }
   }

   /** Search state: a stop together with the line used to arrive there.
    * The start state has no arriving line, so its first arc never counts
    * as a transfer.
    */
   private static final class State {
      /** Current stop. */
      final Vertex stop;
      /** Line of the arc used to arrive at the stop. */
      final String line;
      /** True only for the initial state at the source stop. */
      final boolean start;

      /**
       * Create a state.
       * @param stop current stop
       * @param line arriving line
       * @param start whether this is the initial state
       */
      State (Vertex stop, String line, boolean start) {
         this.stop = stop;
         this.line = line;
         this.start = start;
      }

      @Override
      public boolean equals (Object o) {
         if (!(o instanceof State))
            return false;
         State s = (State) o;
         return stop == s.stop && start == s.start
            && Objects.equals (line, s.line);
      }

      @Override
      public int hashCode() {
         return Objects.hash (System.identityHashCode (stop), line, start);
      }
   }

   /** Priority queue entry ordered by (transfers, arcs). */
   private static final class QueueEntry implements Comparable<QueueEntry> {
      /** State reached. */
      final State state;
      /** Transfers used to reach the state. */
      final int transfers;
      /** Arcs used to reach the state. */
      final int arcs;

      /**
       * Create a queue entry.
       * @param state state reached
       * @param transfers transfers so far
       * @param arcs arcs so far
       */
      QueueEntry (State state, int transfers, int arcs) {
         this.state = state;
         this.transfers = transfers;
         this.arcs = arcs;
      }

      @Override
      public int compareTo (QueueEntry o) {
         if (transfers != o.transfers)
            return Integer.compare (transfers, o.transfers);
         return Integer.compare (arcs, o.arcs);
      }
   }
}
