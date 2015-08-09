import java.lang.NullPointerException;
import java.util.Iterator;

public class Board {
    
    private int N;
    private int[][] _tiles;
    
    // construct a board from an N-by-N array of tiles
    public Board(int[][] tiles) { 
        N = tiles.length;  
        _tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                _tiles[i][j] = tiles[i][j];
            }
        }     
    }
    
    // board dimension N
    public int dimension() {
        return N;
    }
        
    // number of tiles out of place
    public int hamming() {
        int h = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int block = _tiles[i][j];
                if (block != 0 && block != i*N + j + 1)
                    h++;
            }
        }
        return h;
    }
    
    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int v = _tiles[i][j];
                if (v != 0) {
                    int row_off = Math.abs((v - 1) / N - i);
                    int col_off = Math.abs((v - 1) % N - j);
                    manhattan += row_off + col_off;
                }
            }
        }
        
        return manhattan;                
    }
    
    // is this board the goal board?
    public boolean isGoal() {
        return (manhattan() == 0);
    }
    
    // a board that is obtained by exchanging two adjacent tiles in the same row
    public Board twin() {
        
        // pick a row with non zero block
        int row = 0;
        if (_tiles[row][0] == 0 || _tiles[row][1] == 0)
            row++;
        
        Board tw = new Board(_tiles);
        exch(tw._tiles, row, 0, row, 1);
        
        return tw;
    }
    
    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        if (this.dimension() != that.dimension()) return false;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this._tiles[i][j] != that._tiles[i][j]) 
                    return false;
            }
        }
        return true;
    }
    
    // all neighboring boards
    public Iterable<Board> neighbors() {
        
        // find blank                
        int row = -1;
        int col = -1;
        for (int i = 0; i < N; i++) {
          for (int j = 0; j < N; j++) {
              if (_tiles[i][j] == 0) {
                  row = i; col = j;
              }
          }
        }
                             
        // swap neighbors
        Stack<Board> stack = new Stack<Board>();
        if (exch(_tiles, row-1, col, row, col)) {
            stack.push(new Board(_tiles));
            exch(_tiles, row, col, row-1, col); // revert
        }
        if (exch(_tiles, row+1, col, row, col)) {
            stack.push(new Board(_tiles));
            exch(_tiles, row, col, row+1, col); 
        }
        if (exch(_tiles, row, col-1, row, col)) {
            stack.push(new Board(_tiles));
            exch(_tiles, row, col, row, col-1); 
        }
        if (exch(_tiles, row, col+1, row, col)) {
            stack.push(new Board(_tiles));
            exch(_tiles, row, col, row, col+1); 
        }
        
        return stack;
    }
    
    private boolean exch(int[][] tiles, int from_row, int from_col, int to_row, int to_col) {
        if (!isValid(from_row, from_col) || !isValid(to_row, to_col))
            return false;
        
        int temp = tiles[from_row][from_col];
        tiles[from_row][from_col] = tiles[to_row][to_col];
        tiles[to_row][to_col] = temp;
        return true;
    }
    
    private boolean isValid(int row, int col) {
        if (row < 0 || row > N-1) return false;
        if (col < 0 || col > N-1) return false;
        return true;
    }
    
    // string representation of this board (in the output format specified below)
    public String toString() {
         StringBuilder s = new StringBuilder();
         s.append(N + "\n");
         for (int i = 0; i < N; i++) {
             for (int j = 0; j < N; j++) {
                 s.append(String.format("%2d ", _tiles[i][j]));
             }
             s.append("\n");
         }
         return s.toString();
    }

    // unit tests (not graded)
    public static void main(String[] args) {
        
        // 8  1  3        1  2  3     1  2  3  4  5  6  7  8    1  2  3  4  5  6  7  8
        // 4     2        4  5  6     ----------------------    ----------------------
        // 7  6  5        7  8        1  1  0  0  1  1  0  1    1  2  0  0  2  2  0  3
        
        // initial          goal         Hamming = 5 + 0          Manhattan = 10 + 0

        int x[][] = new int[3][3] ;
        x[0][0] =1; x[0][1] = 2; x[0][2] = 3;
        x[1][0] =7; x[1][1] = 0; x[1][2] = 6;
        x[2][0] =5; x[2][1] = 4; x[2][2] = 8;
        Board brd_x = new Board(x);
        StdOut.print(brd_x);
        StdOut.println("****");
        
        StdOut.println("twin *** ");
        StdOut.print(brd_x.twin());
        
        
        int y[][] = new int[3][3] ;
        y[0][0] =8; y[0][1] = 1; y[0][2] = 3;
        y[1][0] =0; y[1][1] = 4; y[1][2] = 2;
        y[2][0] =7; y[2][1] = 6; y[2][2] = 5;
        Board brd_y = new Board(y);
        
        
  
        StdOut.println("dim: " + brd_x.dimension());
        StdOut.println("hamming: " + brd_x.hamming());
        StdOut.println("manhattan: " + brd_x.manhattan());
        StdOut.print("\n********\n");
        
        StdOut.println("Board equal: " + brd_x.equals(brd_y));
        
        StdOut.print(brd_y);
        StdOut.print(brd_x.twin());
        
        
        Iterator<Board> n = brd_x.neighbors().iterator();
        while (n.hasNext()) {
            StdOut.print(n.next());
        }
        
    }
}