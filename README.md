# Java x OpenGL graphics engine

This project is graphics engine made by using Java and LWJGL library that contains OpenGL.

## Description

* Engine capabilities at current state:
  * Window creation
  * Projection setting(ortho/perspective)
  * Loading mesh from .obj file
  * Loading RGB textures
  * Mesh rendering
  * Textured mesh rendering
  * Ambient lighting
  * Diffuse lighting
  * Specular lighting
  * Terrain generation
  * Textured terrain rendering
  * Camera movement(mouse + keyboard)
  * Fog rendering
  * Water rendering(distortion + reflection + refraction)

## Examples

__Mesh rendering__

<img src="https://raw.githubusercontent.com/dan1rock/project-summer-2024/main/Images/1.png">
<img src="https://raw.githubusercontent.com/dan1rock/project-summer-2024/main/Images/2.png">
<img src="https://raw.githubusercontent.com/dan1rock/project-summer-2024/main/Images/3.png">

__Terrain rendering__
<img src="https://raw.githubusercontent.com/dan1rock/project-summer-2024/main/Images/4.png">

__Water rendering with refraction and reflection__
<img src="https://raw.githubusercontent.com/dan1rock/project-summer-2024/main/Images/5.png">

## How to use
>All necessary libraries are in ``libs`` folder, linking of these libraries is required to run project

You can run demo by running ``Main.java`` class

>Before running Main you need to compile classes from ```src``` folder