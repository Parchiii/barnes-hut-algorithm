package Clases;

import java.util.*;
import javafx.geometry.Point2D;

public class SpatialGrid {
    private final int cellSize;
    private final int width;
    private final int height;
    private final Map<GridCell, List<particle>> grid;
    private final List<particle> emptyList = Collections.emptyList();
    
    // Clase interna para representar una celda del grid
    private static class GridCell {
        final int x, y;
        
        GridCell(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridCell gridCell = (GridCell) o;
            return x == gridCell.x && y == gridCell.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
        
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
    
    public SpatialGrid(int cellSize, int width, int height) {
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
        this.grid = new HashMap<>();
    }
    
    /**
     * Actualiza el grid asignando cada partícula a su celda correspondiente
     */
    public void update(List<particle> particles) {
        // Limpiar el grid
        grid.clear();
        
        // Asignar cada partícula a su celda
        for (particle p : particles) {
            GridCell cell = getCellForPosition(p.getPosicion());
            grid.computeIfAbsent(cell, k -> new ArrayList<>()).add(p);
        }
    }
    
    /**
     * Obtiene la celda para una posición dada
     */
    private GridCell getCellForPosition(Point2D position) {
        int cellX = (int)(position.getX() / cellSize);
        int cellY = (int)(position.getY() / cellSize);
        return new GridCell(cellX, cellY);
    }
    
    /**
     * Obtiene todas las partículas en las celdas adyacentes a una partícula dada
     */
    public List<particle> getPotentialCollisions(particle p) {
        List<particle> potentialCollisions = new ArrayList<>();
        GridCell centerCell = getCellForPosition(p.getPosicion());
        
        // Verificar la celda actual y las 8 celdas adyacentes
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                GridCell neighborCell = new GridCell(centerCell.x + dx, centerCell.y + dy);
                List<particle> particlesInCell = grid.getOrDefault(neighborCell, emptyList);
                
                for (particle other : particlesInCell) {
                    if (other != p) { // No comparar consigo misma
                        potentialCollisions.add(other);
                    }
                }
            }
        }
        
        return potentialCollisions;
    }
    
    /**
     * Obtiene partículas cercanas dentro de un radio específico (más eficiente)
     */
    public List<particle> getNeighborsInRadius(particle p, double radius) {
        List<particle> neighbors = new ArrayList<>();
        Point2D pos = p.getPosicion();
        double radiusSq = radius * radius;
        
        GridCell centerCell = getCellForPosition(pos);
        int searchRadius = (int)Math.ceil(radius / cellSize);
        
        // Buscar en un área cuadrada alrededor de la partícula
        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dy = -searchRadius; dy <= searchRadius; dy++) {
                GridCell searchCell = new GridCell(centerCell.x + dx, centerCell.y + dy);
                List<particle> particlesInCell = grid.getOrDefault(searchCell, emptyList);
                
                for (particle other : particlesInCell) {
                    if (other != p) {
                        double dxPos = other.getPosicion().getX() - pos.getX();
                        double dyPos = other.getPosicion().getY() - pos.getY();
                        double distSq = dxPos * dxPos + dyPos * dyPos;
                        if (distSq <= radiusSq) {
                            neighbors.add(other);
                        }
                    }
                }
            }
        }
        
        return neighbors;
    }
    
    /**
     * Método optimizado para detección de colisiones que verifica solo pares únicos
     */
    public void checkAllCollisions(List<particle> particles, CollisionResolver resolver) {
        Set<String> checkedPairs = new HashSet<>();
        
        for (particle p : particles) {
            List<particle> potentialCollisions = getPotentialCollisions(p);
            
            for (particle other : potentialCollisions) {
                // Crear un ID único para el par para evitar verificar dos veces
                String pairId = generatePairId(p, other);
                
                if (!checkedPairs.contains(pairId)) {
                    checkedPairs.add(pairId);
                    
                    // Verificar colisión real
                    double dx = p.getPosicion().getX() - other.getPosicion().getX();
                    double dy = p.getPosicion().getY() - other.getPosicion().getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    
                    if (distance < p.getRadius() + other.getRadius()) {
                        resolver.resolveCollision(p, other);
                    }
                }
            }
        }
    }
    
    /**
     * Genera un ID único para un par de partículas (orden independiente)
     */
    private String generatePairId(particle a, particle b) {
        // Ordenar los "identifiers" para que el par sea único sin importar el orden
        int hashA = System.identityHashCode(a);
        int hashB = System.identityHashCode(b);
        
        if (hashA < hashB) {
            return hashA + "_" + hashB;
        } else {
            return hashB + "_" + hashA;
        }
    }
    
    /**
     * Interfaz para resolver colisiones
     */
    public interface CollisionResolver {
        void resolveCollision(particle a, particle b);
    }
    
    // Métodos de utilidad para debugging
    public void printGridStats() {
        int totalCells = grid.size();
        int totalParticles = grid.values().stream().mapToInt(List::size).sum();
        double avgParticlesPerCell = totalParticles / (double)totalCells;
        
        System.out.printf("SpatialGrid: %d celdas, %d partículas, %.2f partículas/celda%n",
                         totalCells, totalParticles, avgParticlesPerCell);
    }
    
    public Map<GridCell, List<particle>> getGrid() {
        return Collections.unmodifiableMap(grid);
    }
}