import java.time.LocalDateTime;

public record Registro(
        String idUsuario,
        String ruta,
        String estacion,
        Accion accion,
        LocalDateTime timestamp) {
}
