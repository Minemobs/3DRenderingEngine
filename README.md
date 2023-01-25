# Minemobs's 3D Renderer

#### The worst 3D renderer you'll ever see
> **Warning**
> Do not use it in production, it's just a program made for fun.

## How do I create a 3D model ?
You have to create a file called `shape.xml` and follow this example:
<details>
    <summary>Example code</summary>

```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <!-- Head-->
    <cube>
        <beginning>
            <x>-100</x>
            <y>-100</y>
            <z>-100</z>
        </beginning>
        <end>
            <x>100</x>
            <y>100</y>
            <z>100</z>
        </end>
        <color>
            <r>255</r>
            <g>255</g>
            <b>255</b>
        </color>
    </cube>
    <!--Mouth -->
    <square>
        <beginning>
            <x>-50</x>
            <y>60</y>
            <z>-101</z>
        </beginning>
        <end>
            <x>50</x>
            <y>70</y>
            <z>-101</z>
        </end>
        <color>
            <r>0</r>
            <g>0</g>
            <b>0</b>
        </color>
    </square>
    <!-- Nose -->
    <triangle>
        <v1>
            <x>-20</x>
            <y>25</y>
            <z>-101</z>
        </v1>
        <v2>
            <x>0</x>
            <y>-25</y>
            <z>-101</z>
        </v2>
        <v3>
            <x>20</x>
            <y>25</y>
            <z>-101</z>
        </v3>
        <color>
            <r>240</r>
            <g>240</g>
            <b>240</b> 
        </color>
    </triangle>
    <!-- I'm lazy so I'll let you do the rest -->
</root>
```
</details>

## Can I use .obj instead of xml
no.