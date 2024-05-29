#version 330 core

in vec3 FragPos;
in vec3 Normal;
in vec2 TexCoord;
in vec4 RealCoord;
in vec3 LightPos;

out vec4 FragColor;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform vec3 viewPos;
uniform float moveFactor;

const float waveStrength = 0.02;

vec2 clipSpaceToTexCoords(vec4 clipSpace) {
    vec2 ndc = (clipSpace.xy / clipSpace.w);
    vec2 texCoords = ndc / 2.0 + 0.5;
    return clamp(texCoords, 0.002, 0.998);
}

void main() {
    vec2 finalTexCoords = TexCoord;
    vec2 refractionTexCoords = clipSpaceToTexCoords(RealCoord);
    vec2 reflectionTexCoords = vec2(refractionTexCoords.x, 1.0 - refractionTexCoords.y);

    // Sample reflection and refraction textures
    vec4 reflectionColor = texture(reflectionTexture, reflectionTexCoords);
    vec4 refractionColor = texture(refractionTexture, refractionTexCoords);

    // Mix reflection and refraction
    vec3 viewDir = normalize(viewPos - FragPos);
    float fresnelFactor = dot(viewDir, Normal);
    fresnelFactor = clamp(fresnelFactor, 0.0, 1.0);
    fresnelFactor = pow(fresnelFactor, 3.0);

    vec4 waterColor = mix(refractionColor, reflectionColor, 0.5);

    // Calculate lighting
    vec3 lightDir = normalize(LightPos - FragPos);
    float diff = max(dot(Normal, lightDir), 0.0);
    vec3 diffuse = diff * vec3(0.3, 0.3, 0.3);

    FragColor = vec4(waterColor.rgb + diffuse, 1.0);
}