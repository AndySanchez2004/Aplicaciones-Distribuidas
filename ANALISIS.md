# ANALISIS.md

## Prototipo RegistroDistribuido

**Estudiante:** Andy Paul Sánchez Pilaloa  
**Asignatura:** Aplicaciones Distribuidas  
**Curso:** 7mo Software “A”  
**Fecha:** Martes, 2 de junio de 2026  

---

## 1. CAP

El prototipo se acerca a un modelo **AP**, porque cuando un nodo deja de responder, los demás siguen funcionando. Se privilegia la disponibilidad y la tolerancia a fallos de comunicación. No se garantiza consistencia fuerte, porque las operaciones no se replican en todos los nodos.

---

## 2. Falacias consideradas

Se consideró que la red no siempre es confiable, porque un nodo puede caerse o no responder. También se consideró que la latencia no es cero, por eso los heartbeats se envían cada cierto tiempo. Además, TCP no separa mensajes automáticamente, por eso se usó envío y lectura por líneas. Finalmente, se consideró que la red no es segura, por eso se agregó un token básico.

---

## 3. Transparencias

La solución ofrece transparencia de acceso de forma básica, porque el cliente solo envía mensajes y recibe respuestas.

No ofrece transparencia de ubicación completa, porque el cliente conoce el puerto del nodo al que se conecta.

Ofrece transparencia de fallos parcial, porque los nodos detectan caídas, pero el error todavía aparece en consola.

No ofrece transparencia de replicación, porque las operaciones no se copian automáticamente en los tres nodos.

No ofrece transparencia de concurrencia completa, porque no se implementó un control avanzado para varios clientes al mismo tiempo.

---

## 4. SLA

Propongo un SLA de disponibilidad del **99.9%** anual.

El cálculo sería:

**Tiempo de caída = (1 - 0.999) × 8760 = 8.76 horas al año**

Por lo tanto, el sistema podría estar inactivo aproximadamente **8.76 horas al año**.

Si el SLA fuera de **99.99%**, el tiempo de caída sería:

**Tiempo de caída = (1 - 0.9999) × 8760 = 0.876 horas al año**

Esto equivale aproximadamente a **52.6 minutos al año**.

Este cálculo es teórico, porque el prototipo no mide disponibilidad real.

---

## 5. Bully vs Raft

Si se reemplazara Bully por Raft, se ganaría una elección de líder más segura, votación por mayoría y mejor posibilidad de replicar información entre nodos.

El costo sería mayor complejidad, más mensajes entre nodos y más tiempo antes de confirmar operaciones.

En esta práctica se usó Bully porque es más simple y suficiente para demostrar coordinación básica.
