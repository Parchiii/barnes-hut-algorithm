package Clases;

import java.util.List;

public class barnesHutSimulation {
    private quadTree tree;
    private CollisionSystem collisionSystem;
    private double G = 1000;
    private double theta = 0.5;
    private int width = 1920;
    private int height = 1055;
    private double softening = 10.0;
    private double kElectrica = 100000;
    
    public barnesHutSimulation() {
        this.collisionSystem = new CollisionSystem(40, width, height);
    }
    
    public barnesHutSimulation(double G, double theta, int width, int height) {
        this.G = G;
        this.theta = theta;
        this.width = width;
        this.height = height;
        this.collisionSystem = new CollisionSystem(40, width, height);
    }
    
    public void addBody(particle body) {
        collisionSystem.addParticle(body);
    }
    
    public void buildTree() {
        List<particle> particulas = collisionSystem.getParticles();
        regionQuad universe = new regionQuad(width/2, height/2, width, height);
        tree = new quadTree(universe);
        
        for (particle body : particulas) {
            tree.insertar(body);
        }
    }
    
    public void calculateForces() {
        List<particle> particulas = collisionSystem.getParticles();
        
        for (particle body : particulas) {
            body.resetForce();
        }
        
        buildTree();
        
        for (particle body : particulas) {
            tree.calcularFuerzas(body, theta, G, softening);
            
            if(body instanceof chargedParticle) {
                calcularFuerzasElectricas((chargedParticle) body);
            }
        }
    }
    
    private void calcularFuerzasElectricas(chargedParticle particula) {
        List<particle> vecinos = collisionSystem.getSpatialGrid()
                                .getNeighborsInRadius(particula, 100);
        
        for (particle otra : vecinos) {
            if (otra instanceof chargedParticle && otra != particula) {
                chargedParticle otraCargada = (chargedParticle) otra;
                particula.addFuerzaElectrica(otraCargada, kElectrica);
            }
        }
    }
    
    public void updateBodies(double dt) {
        List<particle> particulas = collisionSystem.getParticles();
        for (particle body : particulas) {
            body.actualizar(dt);
        }
    }
    
    public void simulateStep(double dt) {
        calculateForces();
        updateBodies(dt);
        collisionSystem.updateAndDetectCollisions();
    }
    
    // Getters y Setters
    public double getG() { return G; }
    public void setG(double G) { this.G = G; }
    public double getTheta() { return theta; }
    public void setTheta(double theta) { this.theta = theta; }
    public double getSoftening() { return softening; }
    public void setSoftening(double softening) { this.softening = softening; }
    public double getKElectrica() { return kElectrica; }
    public void setKElectrica(double k) { this.kElectrica = k; }
    public List<particle> getParticulas() { 
        return collisionSystem.getParticles(); 
    }
    
    // Método para reiniciar todas las partículas
    public void clearParticles() {
        collisionSystem.getParticles().clear();
    }
    
    // Método para obtener estadísticas
    public int getParticleCount() {
        return collisionSystem.getParticles().size();
    }
}