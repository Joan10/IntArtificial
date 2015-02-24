package agents;

// Exemple de Bitxo

public class Bitxo1 extends Agent
{
    static final boolean DEBUG = false;

    final int PARET = 0;
    final int NAU   = 1;
    final int RES   = -1;
    final int hola = 5;
    final int ESQUERRA = 0;
    final int CENTRAL  = 1;
    final int DRETA    = 2;
    final int DISTANCIA_MINIMA = 45;

    Estat estat;
    int espera = 0;

    
    long temps;

    public Bitxo1(Agents pare) {
        super(pare, "Bitxo1", "imatges/robotank2.gif");
    }

    @Override
    public void inicia()
    {
        setAngleVisors(30);
        setDistanciaVisors(350);
        setVelocitatLineal(5);
        setVelocitatAngular(5);
        espera = 0;
        temps = 0;
    }

    @Override
    public void avaluaComportament()
    {
        temps++;
        estat = estatCombat();
        if (espera > 0) {
            if (DEBUG) System.out.println("Espera "+espera);
            espera--;
        }
        else
        {
            atura();
            if (estat.enCollisio) // situació de nau bloquejada
            {
                // si veu la nau, dispara
                if (estat.objecteVisor[CENTRAL] == NAU && estat.impactesRival < 5)
                {
                    dispara();   //bloqueig per nau, no giris dispara
                }
                else // hi ha un obstacle, gira i parteix
                {
                    gira(20); // 20 graus
                    if (hiHaParedDavant(20)) enrere();
                    else endavant();
                    espera=8;
                }
            } 
            else {
                endavant();
                if (estat.veigEnemic)
                {
                    if (estat.sector == 2 || estat.sector == 3)
                    {
                        mira(estat.posicioEnemic.x, estat.posicioEnemic.y);
                        if (estat.impactesRival < 5) dispara();
                    }
                    else if (estat.sector == 1)
                    {
                        dreta();
                    }
                    else
                    {
                        esquerra();
                    }
                }

                // Miram els visors per detectar els obstacles
                int sensor = 0;

                if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA] < DISTANCIA_MINIMA) {
                    sensor += 1;
                }
                if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL] < DISTANCIA_MINIMA) {
                    sensor += 2;
                }
                if (estat.objecteVisor[ESQUERRA] == PARET && estat.distanciaVisors[ESQUERRA] < DISTANCIA_MINIMA) {
                    sensor += 4;
                }
                if (DEBUG) System.out.println("Sensor: " + sensor);
                
                switch (sensor) {
                    case 0: endavant();
                        break;
                    case 1: // dreta bloquejada
                    case 3: esquerra();                        
                        break;
                    case 4: // esquerra bloquejada
                    case 6: dreta();                        
                        break;
                    case 5: // centre lliure
                        endavant();
                        break;  
                    case 2:  // paret devant
                    case 7:  // si estic molt aprop, torna enrere
                        double distancia;
                        distancia = minimaDistanciaVisors();

                        if (distancia < 15) {
                            espera = 8;
                            enrere();
                        } else // gira aleatòriament a la dreta o a l'esquerra
                            dreta();
                        break;
                }
            }
        }
    }

    boolean hiHaParedDavant(int dist)
    {

       if (estat.objecteVisor[ESQUERRA]== PARET && estat.distanciaVisors[ESQUERRA]<=dist)
           return true;

       if (estat.objecteVisor[CENTRAL ]== PARET && estat.distanciaVisors[CENTRAL ]<=dist)
           return true;

       if (estat.objecteVisor[DRETA   ]== PARET && estat.distanciaVisors[DRETA   ]<=dist)
           return true;
       
       return false;
    }

    double minimaDistanciaVisors()
    {
        double minim;

        minim = Double.POSITIVE_INFINITY;
        if (estat.objecteVisor[ESQUERRA] == PARET)
            minim = estat.distanciaVisors[ESQUERRA];
        if (estat.objecteVisor[CENTRAL] == PARET && estat.distanciaVisors[CENTRAL]<minim)
            minim = estat.distanciaVisors[CENTRAL];
        if (estat.objecteVisor[DRETA] == PARET && estat.distanciaVisors[DRETA]<minim)
            minim = estat.distanciaVisors[DRETA];
        return minim;
    }
}