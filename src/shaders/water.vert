#version 330 core

const float PI = 3.1415926535897932384626433832795;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec3 inNormal;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord;
out vec4 RealCoord;
out vec4 GridCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float waveTime;
uniform float waveLength;
uniform float waveAmplitude;
uniform float distortionScale;

vec3 calcNormal(vec3 vertex0, vec3 vertex1, vec3 vertex2){
    vec3 tangent = vertex1 - vertex0;
    vec3 bitangent = vertex2 - vertex0;
    return normalize(cross(tangent, bitangent));
}

float generateOffset(float x, float z, float val1, float val2){
    return waveAmplitude * 0.5 * (sin(x + waveTime * val1) + cos(z * waveLength + waveTime * val2) - 2);
}

vec3 applyDistortion(vec3 vertex){
    float xDistortion = generateOffset(vertex.x * distortionScale, vertex.z * distortionScale, 2, 1);
    float yDistortion = generateOffset(vertex.x * distortionScale, vertex.z * distortionScale, 1, 3);
    float zDistortion = generateOffset(vertex.x * distortionScale, vertex.z * distortionScale, 1.5, 2);
    return vertex + vec3(xDistortion, yDistortion, zDistortion);
}

void main() {
    vec3 currentVertex = inPosition;
    vec3 vertex1 = currentVertex + vec3(0.1, 0.0, 0.1);
    vec3 vertex2 = currentVertex + vec3(0.1, 0.0, -0.1);

    GridCoord = projection * view * model * vec4(currentVertex, 1.0);

    currentVertex = applyDistortion(currentVertex);
    vertex1 = applyDistortion(vertex1);
    vertex2 = applyDistortion(vertex2);

    Normal = calcNormal(currentVertex, vertex1, vertex2);

    RealCoord = projection * view * model * vec4(currentVertex, 1.0);

    FragPos = vec3(model * vec4(inPosition, 1.0));
    TexCoord = inTexCoord;

    gl_Position = RealCoord;
}