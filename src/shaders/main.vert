#version 330 core
layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec3 inNormal;

out vec2 TexCoord;
out vec3 FragPos;
out vec3 Normal;
out vec4 FragPosLightSpace;

uniform vec4 clipPlane;
uniform bool useClipPlane;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 lightSpace;

void main() {
    if (useClipPlane) {
        vec4 worldPosition = model * vec4(inPosition, 1.0);
        gl_ClipDistance[0] = dot(worldPosition, clipPlane);
    }
    gl_Position = projection * view * model * vec4(inPosition, 1.0);
    vec4 fragPos = model * vec4(inPosition, 1.0);
    FragPos = fragPos.xyz;
    FragPosLightSpace = lightSpace * fragPos;
    Normal = mat3(transpose(inverse(model))) * inNormal;
    TexCoord = inTexCoord;
}