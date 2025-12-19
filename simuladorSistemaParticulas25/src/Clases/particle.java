package Clases;

import java.util.Random;
import javafx.geometry.Point2D;

public class particle {
    protected Point2D pos;
    protected Point2D vel;
    protected Point2D netForce;
    protected double mass;
    protected Random random = new Random();
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1055;
    protected double radius = 2;
    
    public particle () {
        this.mass = 1;
        this.pos = new Point2D(random.nextDouble() * WIDTH, random.nextDouble() * HEIGHT);
        this.vel = new Point2D(0, 0);
        this.netForce = new Point2D(0, 0);
    }
    public particle (double mass){
        this.mass = mass;
        this.pos = new Point2D(random.nextDouble() * WIDTH, random.nextDouble() * HEIGHT);
        this.vel = new Point2D(0, 0);
        this.netForce = new Point2D(0, 0);
    }
    public particle (double mass, Point2D newpos){
        this.mass = mass;
        this.pos = newpos;
        this.vel = new Point2D(0,0);
        this.netForce = new Point2D(0,0);
    }
    public particle (double mass,Point2D newpos, Point2D newvel){
        this.mass = mass;
        this.pos = newpos;
        this.vel = newvel;
        this.netForce = new Point2D(0,0);
    }
    public particle (double mass, Point2D newpos, Point2D newvel, Point2D newNetForce){
        this.mass = mass;
        this.pos = newpos;
        this.vel = newvel;
        this.netForce = newNetForce;
    }
    public Point2D getPosicion(){return this.pos;}
    public double getMass(){return this.mass;}
    public Point2D getNetForce() { return this.netForce; }
    public Point2D getVelocidad() { return this.vel; }
    public double getRadius() { return this.radius; }
    public void setNetForce(Point2D newNetForce){this.netForce = newNetForce;}
    public void setAcc (Point2D newacc){this.netForce = newacc.multiply(this.mass);}
    public void setVelocidad(Point2D nuevaVel) { this.vel = nuevaVel; }
    public void setRadius(double radius) { this.radius = radius; }
    public void addGravitacionalForce(particle otro, double G, double softening) {
        double dx = otro.getPosicion().getX() - this.pos.getX();
        double dy = otro.getPosicion().getY() - this.pos.getY();
        double r = Math.sqrt(dx*dx + dy*dy);
        
        if (r == 0) return;
        
        // Softening parameter para evitar fuerzas infinitas
        double rSuave = Math.sqrt(r*r + softening*softening);
        double fuerzaGravitacional = (G * this.mass * otro.mass) / (rSuave * rSuave);
        
        this.netForce = this.netForce.add(
            new Point2D(fuerzaGravitacional * dx / rSuave,
                        fuerzaGravitacional * dy / rSuave)
        );
    }
    public void actualizar(double deltatime){
        vel = vel.add(netForce.multiply(deltatime).multiply(1/mass));
        pos = pos.add(vel.multiply(deltatime));
    }
    public void resetForce(){this.netForce = new Point2D(0,0);}
    public boolean isCollidingWith(particle other) {
        double dx = this.pos.getX() - other.pos.getX();
        double dy = this.pos.getY() - other.pos.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (this.radius + other.radius);
    }
    public double timeToCollision(particle other) {
        Point2D relativePos = other.pos.subtract(this.pos);
        Point2D relativeVel = other.vel.subtract(this.vel);
        
        double a = relativeVel.dotProduct(relativeVel);
        double b = 2 * relativePos.dotProduct(relativeVel);
        double c = relativePos.dotProduct(relativePos) - 
                  (this.radius + other.radius) * (this.radius + other.radius);
        
        double discriminant = b * b - 4 * a * c;
        
        if (discriminant < 0) {
            return Double.POSITIVE_INFINITY; // No hay colisión
        }
        
        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);
        
        // Devolver el tiempo positivo más pequeño
        if (t1 > 0) return t1;
        if (t2 > 0) return t2;
        return Double.POSITIVE_INFINITY;
    }
    public void colisionarCon(particle otra) {
        Point2D deltaPos = this.pos.subtract(otra.pos);
        double distancia = deltaPos.magnitude();
        
        if (distancia == 0) return;
        
        // Vector normalizado de la colisión
        Point2D normal = deltaPos.multiply(1.0 / distancia);
        
        // Velocidades relativas
        Point2D velRelativa = this.vel.subtract(otra.vel);
        double velocidadNormal = velRelativa.dotProduct(normal);
        
        // Si se están alejando, no colisionar
        if (velocidadNormal > 0) return;
        
        // Coeficiente de restitución (elástico)
        double restitution = 0.9;
        
        // Impulso
        double impulso = -(1 + restitution) * velocidadNormal;
        impulso /= (1/this.mass + 1/otra.mass);
        
        // Aplicar impulso
        Point2D impulsoVector = normal.multiply(impulso);
        this.vel = this.vel.add(impulsoVector.multiply(1/this.mass));
        otra.vel = otra.vel.subtract(impulsoVector.multiply(1/otra.mass));
        
        // Separar partículas para evitar superposición
        double overlap = (this.radius + otra.radius) - distancia;
        if (overlap > 0) {
            Point2D correction = normal.multiply(overlap * 0.5);
            this.pos = this.pos.add(correction);
            otra.pos = otra.pos.subtract(correction);
        }
    }
    public void manejarColisionBordes() {
    double restitution = 0.7;
    
    // Colisión con bordes horizontales
    if (pos.getX() - radius < 0) {
        pos = new Point2D(radius, pos.getY());
        vel = new Point2D(-vel.getX() * restitution, vel.getY());
    } else if (pos.getX() + radius > WIDTH) {
        pos = new Point2D(WIDTH - radius, pos.getY());
        vel = new Point2D(-vel.getX() * restitution, vel.getY());
    }
    
    // Colisión con bordes verticales
    if (pos.getY() - radius < 0) {
        pos = new Point2D(pos.getX(), radius);
        vel = new Point2D(vel.getX(), -vel.getY() * restitution);
    } else if (pos.getY() + radius > HEIGHT) {
        pos = new Point2D(pos.getX(), HEIGHT - radius);
        vel = new Point2D(vel.getX(), -vel.getY() * restitution);
    }
}
}
