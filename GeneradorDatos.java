import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GeneradorDatos {

    private static final List<String> ESTACIONES = List.of(
            "Central", "Norte", "Sur", "Universidad",
            "Hospital", "Terminal", "Parque", "Aeropuerto");

    private static final List<String> RUTAS = List.of("R10", "R20", "R30", "R40", "R50");

    //
    private static final List<Integer> HORAS = List.of(
            6, 7, 7, 7, 8, 8, 9, 12, 13, 17, 17, 17, 18, 18, 19, 21);

    private static final LocalDate DIA = LocalDate.of(2026, 11, 4);

    public static List<Registro> generar(int usuarios, long semilla) {
        Random random = new Random(semilla);
        return IntStream.rangeClosed(1, usuarios)
                .boxed()
                .flatMap(n -> viajeDeUsuario ("U" + n, random))
                .toList();
    }

    private static Stream<Registro> viajeDeUsuario(String idUsuario, Random random) {
        String ruta = RUTAS.get(random.nextInt(RUTAS.size()));
        int paradas = 2 + random.nextInt(4);
        int estacionInicial = random.nextInt(ESTACIONES.size());
        LocalDateTime inicio = DIA.atTime(HORAS.get(random.nextInt(HORAS.size())), random.nextInt(60));

        return IntStream.range(0, paradas)
                .boxed()
                .flatMap(i -> {
                    String estacion = ESTACIONES.get((estacionInicial + i) % ESTACIONES.size());
                    LocalDateTime llegada = inicio.plusMinutes(i * 10L);
                    return Stream.of(
                            new Registro(idUsuario, ruta, estacion, Accion.ENTRADA, llegada),
                            new Registro(idUsuario, ruta, estacion, Accion.SALIDA, llegada.plusMinutes(1)));
                });
    }
}
