import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Registro> registros = GeneradorDatos.generar(10_000, 42L);
        System.out.println("Total de registros: " + registros.size());

        System.out.println("\n=== a) Afluencia por estación ===");
        AnalisisTransporte.afluenciaPorEstacion(registros).entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        System.out.println("\n=== b) Top 3 horas pico ===");
        AnalisisTransporte.horasPico(registros, 3)
                .forEach(e -> System.out.println(e.getKey() + ":00 -> " + e.getValue()));

        System.out.println("\n=== c) Top 3 rutas más usadas ===");
        AnalisisTransporte.rutasMasUsadas(registros, 3)
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));

        System.out.println("\n=== d) Patrones de viaje (primeros 5 usuarios) ===");
        AnalisisTransporte.patronesDeViaje(registros).entrySet().stream()
                .limit(5)
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));

        System.out.println("\n=== e) Tiempo promedio entre estaciones (minutos) ===");
        AnalisisTransporte.tiempoPromedioEntreEstaciones(registros)
                .forEach((tramo, minutos) -> System.out.printf("%s: %.1f%n", tramo, minutos));

        long umbral = 7_000;
        System.out.println("\n=== f) Estado de rutas (umbral = " + umbral + ") ===");
        AnalisisTransporte.estadoPorRuta(registros, umbral)
                .forEach((ruta, estado) -> System.out.println(ruta + ": " + estado));
        System.out.println("Rutas críticas: " + AnalisisTransporte.rutasCriticas(registros, umbral));

        System.out.println("\n=== Secuencial vs paralelo (1.400.000 registros aprox.) ===");
        List<Registro> masivo = GeneradorDatos.generar(200_000, 7L);

        long t0 = System.nanoTime();
        Map<String, Long> secuencial = AnalisisTransporte.afluenciaPorEstacion(masivo, false);
        long t1 = System.nanoTime();
        Map<String, Long> paralelo = AnalisisTransporte.afluenciaPorEstacion(masivo, true);
        long t2 = System.nanoTime();

        System.out.println("Registros procesados: " + masivo.size());
        System.out.println("Secuencial: " + (t1 - t0) / 1_000_000 + " ms");
        System.out.println("Paralelo:   " + (t2 - t1) / 1_000_000 + " ms");
        System.out.println("¿Mismo resultado? " + secuencial.equals(paralelo));
    }
}