import java.util.Comparator;
public class Solver {  // Uses A* algorithm to solve 8Puzzle
    private boolean      _isSolvable;
    private SearchNode   _goal;
    
    private static class SearchNode {
        private Board      _board;
        private SearchNode _prev;
        private int        _moves;
        public SearchNode(Board b, SearchNode prev, int moves) {
            _board = b;
            _prev = prev;
            _moves = moves;
        }
        
        private static final Comparator<SearchNode> BY_PRIORITY = new PriorityOrder();           
        private static class PriorityOrder implements Comparator<SearchNode> {
            public int compare(SearchNode a, SearchNode b) {
                if (a == null || b == null) throw  new NullPointerException();
                int pa = a._board.manhattan() + a._moves;
                int pb = b._board.manhattan() + b._moves;
                if (pa < pb) return -1;
                if (pa > pb) return +1;    
                return 0;
            }
        };
    }
     
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>(SearchNode.BY_PRIORITY);
        MinPQ<SearchNode> pq_tw = new MinPQ<SearchNode>(SearchNode.BY_PRIORITY);
        
        pq.insert(new SearchNode(initial, null, 0)); // Board(current board, previous sn, moves)
        pq_tw.insert(new SearchNode(initial.twin(), null, 0));
        
        SearchNode curr = pq.delMin();
        SearchNode curr_tw = pq_tw.delMin();      
        // solve initial and twin parallely and exit if any one finished. 
        while ( !curr._board.isGoal() && !curr_tw._board.isGoal() ) {
            
            for (Board n : curr._board.neighbors()) {
                // critical optimization
                if (curr._prev != null && n.equals(curr._prev._board))
                    continue;
                
                pq.insert(new SearchNode(n, curr, curr._moves + 1));
            }
            
            for (Board n : curr_tw._board.neighbors()) {
                // critical optimization
                if (curr_tw._prev != null && n.equals(curr_tw._prev._board))
                    continue;
                
                pq_tw.insert(new SearchNode(n, curr_tw, curr_tw._moves + 1));
            }
            
            curr = pq.delMin();
            curr_tw = pq_tw.delMin();
        }
        
        // If twin is solvable, current board is not (and vice-versa)
        _isSolvable = !curr_tw._board.isGoal();
        if (_isSolvable) _goal = curr;
        else             _goal = null;
    }
    
    // is the initial board solvable?
    public boolean isSolvable() {
        return _isSolvable;
    }
    
    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        return _goal._moves; 
    }
    
    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> solution = new Stack<Board>();
        SearchNode curr = _goal;
        while (curr != null) {
            solution.push(curr._board);
            curr = curr._prev;
        }
        return solution;
    }
    
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        
        Board initial = new Board(blocks);
        
        // solve the puzzle
        Solver solver = new Solver(initial);
        
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}