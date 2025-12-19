package Clases;

import javafx.geometry.Point2D;

public class chargedParticle extends particle {
    private double charge;
    
    public chargedParticle (double mass, double charge) {
        super(mass);
        this.charge = charge;
    }
    public chargedParticle (double mass, double charge, Point2D pos){
        super(mass, pos);
        this.charge = charge;
    }
    public chargedParticle(double mass, double charge, Point2D pos, Point2D vel) {
        super(mass, pos, vel);
        this.charge = charge;
    }
    public double getCharge() {
        return charge;
    }
    public void setCharge(double charge) {
        this.charge = charge;
    }
    public void addFuerzaElectrica(chargedParticle otro, double k) {
        double dx = otro.getPosicion().getX() - this.pos.getX();
        double dy = otro.getPosicion().getY() - this.pos.getY();
        double r = Math.sqrt(dx*dx + dy*dy);
        
        if (r == 0) return;
        
        // Softening parameter
        double rSuave = Math.sqrt(r*r + 10.0);
        double fuerzaElectrica = (k * this.charge * otro.charge) / (rSuave * rSuave);
        
        this.netForce = this.netForce.add(
            new Point2D(fuerzaElectrica * dx / rSuave,
                        fuerzaElectrica * dy / rSuave)
        );
    }
        @Override
    public void colisionarCon(particle otra) {
        // Para partículas cargadas, podemos agregar repulsión adicional
        if (otra instanceof chargedParticle) {
            chargedParticle otraCargada = (chargedParticle) otra;
            // Repulsión eléctrica a muy corta distancia
            Point2D deltaPos = this.pos.subtract(otraCargada.pos);
            double distancia = deltaPos.magnitude();
            
            if (distancia < this.radius + otraCargada.radius + 5) {
                double fuerzaRepulsion = 1000.0 * this.charge * otraCargada.charge / (distancia * distancia);
                Point2D direccion = deltaPos.normalize();
                this.netForce = this.netForce.add(direccion.multiply(fuerzaRepulsion));
            }
        }
        
        // Llamar a la colisión normal
        super.colisionarCon(otra);
    }
}
