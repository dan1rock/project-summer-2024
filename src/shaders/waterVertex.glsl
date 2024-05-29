#version 330 core

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec3 inNormal;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord;
out vec4 RealCoord;
out vec3 LightPos;

uniform vec3 lightPos;
uniform mat4 model;
uniform mat4 projection;

void main() {
    FragPos = vec3(model * vec4(inPosition, 1.0));
    Normal = mat3(transpose(inverse(model))) * inNormal;
    TexCoord = inTexCoord;

    RealCoord = projection * vec4(FragPos, 1.0);
    LightPos = vec3(model * vec4(lightPos, 1.0));
    gl_Position = RealCoord;
}