# :fuelpump: Neraby-shops  :fuelpump:
## Descripción :blue_book:
El objetivo es la realización de una aplicación que consuma un servicio determinado y muestre dos pantallas distintas: un listado de comercios y una vista de detalle de cada uno de ellos.

## Recursos necesarios :open_file_folder:

* Assets para la prueba -> [prueba_assets.zip](https://github.com/sam-sudo/Neraby-shops/files/14546138/prueba_assets.zip)

* La url del servicio a consultar es:  https://waylet-web-export.s3.eu-west-1.amazonaws.com/commerces.json

## Interfaz esperada
<img src="https://github.com/sam-sudo/Neraby-shops/assets/57421143/f3c479fd-937f-4d66-a651-d8d93af5db3a"  width="200" height="400" />
<img src="https://github.com/sam-sudo/Neraby-shops/assets/57421143/ae238ee3-17e4-4e99-864d-8732b62cec9b"  width="200" height="400" />

## Requisitos :lock:

- [ x ] Los comercios deben poder filtrarse por categoría 
- [ x ] Los comercios deben poder ordenarse por distancia al usuario 
- [ ] La pantalla de detalle de un comercio debe incluir el nombre del mismo, los principales servicios, datos de contacto y un mapa que muestre la localización de dicho comercio 
- [ ] Se debe configurar un deeplink del tipo `ptklikin://detalle/:id`. Al pulsarlo desde fuera de la app, esta se debe abrir y debe conducir al detalle del comercio que corresponda con el parámetro `:id`. Por ejemplo, si la url es `ptklikin://detalle/12abfc34`, la app debe abrirse y mostrar el detalle del comercio con `id 12abfc34`
- [ x ] No puede construirse con Compose
* Verás que los campos de texto contienen "Lorem ipsum...". La idea es que en esos "placeholders" incluyas la información que consideres más adecuada. Solemos trabajar con espaciados múltiplos de 4pt: 
  * 4 para los espaciados mínimos
  * 8 para los normales
  * 16 para los grandes, por ejemplo los bordes de la pantalla

## Extras valorables :unlock:

* Cumplimiento de los requisitos expuestos anteriormente
* Estructura y organización del proyecto
* Uso justificado de dependencias externas
* Arquitectura y patrones de diseño empleados

## Tecnologías usadas
