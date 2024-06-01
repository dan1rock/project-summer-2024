#version 330 core

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord;
in vec4 RealCoord;
in vec4 GridCoord;
in vec3 LightPos;
in vec3 ViewPos;

out vec4 FragColor;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform float moveFactor;
uniform vec3 viewPos;
uniform vec3 lightColor;

uniform float ambientStrength;
uniform float shininess;
uniform float specularStrength;

const float waveStrength = 0.02;

vec2 clipSpaceToTexCoords(vec4 clipSpace) {
    vec2 ndc = (clipSpace.xy / clipSpace.w);
    vec2 texCoords = ndc / 2.0 + 0.5;
    return clamp(texCoords, 0.002, 0.998);
}

void main() {
    vec2 finalTexCoords = TexCoord;
    vec2 refractionTexCoords = clipSpaceToTexCoords(RealCoord);
    vec2 reflectionTexCoords = clipSpaceToTexCoords(GridCoord);
    reflectionTexCoords = vec2(reflectionTexCoords.x, 1.0 - reflectionTexCoords.y);

    // Sample reflection and refraction textures
    vec4 reflectionColor = texture(reflectionTexture, reflectionTexCoords);
    vec4 refractionColor = texture(refractionTexture, refractionTexCoords);

    // Mix reflection and refraction
    vec3 viewDir = normalize(viewPos - FragPos);
    float fresnelFactor = dot(viewDir, Normal);
    fresnelFactor = clamp(fresnelFactor, 0.0, 1.0);
    fresnelFactor = pow(fresnelFactor, 3.0);

    vec4 waterColor = mix(refractionColor, reflectionColor, 0.5);

    // Ambient lighting
    vec3 ambient = ambientStrength * lightColor;

    // Diffuse lighting
    vec3 lightDir = normalize(LightPos - FragPos);
    float diff = max(dot(Normal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    // Specular lighting
    viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, Normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = specularStrength * spec * lightColor;


    FragColor = vec4((ambient + diffuse + specular) * waterColor.rgb, 1.0);
}