#version 330 core
in vec2 TexCoord;
in vec3 FragPos;
in vec3 LightPos;
in vec3 Normal;
in vec4 ViewPos;

out vec4 FragColor;

uniform sampler2D textureSampler;

uniform vec3 viewPos;
uniform vec3 lightColor;
uniform vec3 objectColor;
uniform vec3 fogColor;

uniform float ambientStrength;
uniform float shininess;
uniform float specularStrength;
uniform float fogStart;
uniform float fogEnd;

uniform bool isTextured;

void main() {
    vec4 texColor;
    if (isTextured) {
        texColor = texture(textureSampler, TexCoord);
    } else {
        texColor = vec4(objectColor, 1.0);
    }

    // Ambient lighting
    vec3 ambient = ambientStrength * lightColor;

    // Diffuse lighting
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(LightPos - FragPos);
    float diff = (dot(norm, lightDir) + 0.5) / 1.5;
    vec3 diffuse = diff * lightColor;

    // Specular lighting
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = specularStrength * spec * lightColor;

    // Combine all the lighting components
    vec3 lighting = (ambient + diffuse + specular) * objectColor;
    vec4 result = vec4(lighting, 1.0) * texColor;

    // Calculate the fog factor
    float distance = length(ViewPos.xyz);
    float fogFactor = clamp((fogEnd - distance) / (fogEnd - fogStart), 0.0, 1.0);

    vec3 finalColor = mix(fogColor, result.rgb, fogFactor);

    FragColor = vec4(finalColor, result.a);
}