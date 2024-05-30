#version 330 core

const float PI = 3.1415926535897932384626433832795;

const float waveLength = 20.0;
const float waveAmplitude = 0.1;
const float shineDamper = 20.0;

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec3 inNormal;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord;
out vec4 RealCoord;
out vec4 GridCoord;
out vec3 LightPos;
out vec3 ViewPos;

uniform vec3 viewPos;
uniform vec3 lightPos;
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float waveTime;

vec3 calcNormal(vec3 vertex0, vec3 vertex1, vec3 vertex2){
    vec3 tangent = vertex1 - vertex0;
    vec3 bitangent = vertex2 - vertex0;
    return normalize(cross(tangent, bitangent));
}

float generateOffset(float x, float z, float val1, float val2){
    float radiansX = ((mod(x+z*x*val1, waveLength)/waveLength) + waveTime * mod(x * 0.8 + z, 1.5)) * 2.0 * PI;
    float radiansZ = ((mod(val2 * (z*x +x*z), waveLength)/waveLength) + waveTime * 2.0 * mod(x , 2.0) ) * 2.0 * PI;
    return waveAmplitude * 0.5 * (sin(radiansZ) + cos(radiansX) - 2);
}

vec3 applyDistortion(vec3 vertex){
    float xDistortion = generateOffset(vertex.x * 0.1, vertex.z * 0.1, 0.2, 0.1);
    float yDistortion = generateOffset(vertex.x * 0.1, vertex.z * 0.1, 0.1, 0.3);
    float zDistortion = generateOffset(vertex.x * 0.1, vertex.z * 0.1, 0.15, 0.2);
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

    FragPos = vec3(view * model * vec4(inPosition, 1.0));
    TexCoord = inTexCoord;

    LightPos = vec3(model * vec4(lightPos, 1.0));
    ViewPos = vec3(view * vec4(viewPos, 1.0));
    gl_Position = RealCoord;
}