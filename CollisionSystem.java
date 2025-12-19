package Clases;

import java.util.ArrayList;
import java.util.List;

public class CollisionSystem {
    private final SpatialGrid spatialGrid;
    private final List<particle> particles;
    private final int width;
    private final int height;
    
    public CollisionSystem(int cellSize, int width, int height) {
        this.spatialGrid = new SpatialGrid(cellSize, width, height);
        this.particles = new ArrayList<>();
        this.width = width;
        this.height = height;
    }
    
    public void addParticle(particle p) {
        particles.add(p);
    }
    
    public List<particle> getParticles() {
        return particles;
    }
    
    public SpatialGrid getSpatialGrid() {
        return spatialGrid;
    }
    
    /**
     * Actualiza y detecta todas las colisiones
     */
    public void updateAndDetectCollisions() {
        // Actualizar el spatial grid
        spatialGrid.update(particles);
        
        // Detectar y resolver colisiones usando el grid
        spatialGrid.checkAllCollisions(particles, new SpatialGrid.CollisionResolver() {
            @Override
            public void resolveCollision(particle a, particle b) {
                resolveParticleCollision(a, b);
            }
        });
        
        // Manejar colisiones con bordes
        handleBoundaryCollisions();
    }
    
    /**
     * Resuelve colisión entre dos partículas
     */
    private void resolveParticleCollision(particle a, particle b) {
        // Usar el método de colisión de la clase particle
        a.colisionarCon(b);
    }
    
    /**
     * Maneja colisiones con los bordes de la pantalla
     */
    private void handleBoundaryCollisions() {
        for (particle p : particles) {
            p.manejarColisionBordes();
        }
    }
}